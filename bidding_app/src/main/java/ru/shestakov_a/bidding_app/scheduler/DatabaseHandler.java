package ru.shestakov_a.bidding_app.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import real_estate.entity.RealEstateEntity;
import real_estate.parser.RealEstateParser;
import ru.shestakov_a.bidding_app.service.RealEstateService;
import util.DownloaderJsonsHelper;
import util.ConnectionHelper;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Класс планировщика, который заполняет и очищает базу данных в зависимости от актуальности данных
 * @author Shestakov Artem
 * */
@Service
@Slf4j
public class DatabaseHandler {
    private static final long DATABASE_PROCESSING_DAY = 1L;
    private static final long YESTERDAY = 1L;
    private static final String ZONE_ID_PREFIX = "UTC";

    @Value("${scheduler.pattern.url}")
    private String urlPatternForDownload;

    @Value("${scheduler.n-threads}")
    private int nThreads;

    @Value("${scheduler.timezone}")
    private String utcTimeZone;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private RealEstateService realEstateService;

    /**
     * Метод заполняет базу данных по расписанию заданному в
     * {@see application.properties}
     * */
    @Scheduled(cron = "${interval-in-cron.fill}")
//    @Scheduled(fixedDelay = 80000)
    public void fillDb() {
        String url = getUrl();
        log.info("Загрузка данные с url-адреса: {}", url);
        List<RealEstateEntity> realEstateEntityList = null;
        try {
            List<String> links = DownloaderJsonsHelper.getDailyDownloadLinkByObjects(url);
//            ExecutorService service = Executors.newSingleThreadExecutor();
            ExecutorService service = Executors.newFixedThreadPool(nThreads);

            realEstateEntityList = getRealEstateEntityList(links, service);
        } catch (NoSuchAlgorithmException | IOException | KeyManagementException e) {
            log.warn("Ошибка {} при добавлении данных: {}", e.getClass().getName(), e.getMessage());
        }

        if (realEstateEntityList != null && !realEstateEntityList.isEmpty()) {
            realEstateService.saveAllRealEstate(realEstateEntityList);
        }
    }

    /**
     * Метод удаляет устаревшие данные
     * */
//    @Scheduled(fixedDelay = 60000)
    @Scheduled(cron = "${interval-in-cron.clear}")
    public void clearDb() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.ofOffset(ZONE_ID_PREFIX, ZoneOffset.of(utcTimeZone))); //"+03"
        LocalDateTime date = localDateTime.minusDays(DATABASE_PROCESSING_DAY);
        log.info("Удаление данных на дату: {}", date);
        realEstateService.deleteOldRealEstate(date);

    }

    /**
     * Метод генерирует URL для скачки новых данных
    * */
    private String getUrl() {
        System.out.println("utcTimeZone = " + utcTimeZone);
        LocalDateTime currentDate = LocalDateTime.now(ZoneId.ofOffset(ZONE_ID_PREFIX, ZoneOffset.of(utcTimeZone))).minusDays(YESTERDAY);
        LocalDateTime previousDate = currentDate.minusDays(YESTERDAY);
        return String.format(urlPatternForDownload, previousDate.format(formatter), currentDate.format(formatter));
    }

    /**
     * @param links - ссылки для обработки JSON с данными по объектам недвижимости
     * @param service - возможная реализация многопоточности при обработки данных
     * @return Список сущностей объектов недвижимости после обработки JSON
     * */
    private List<RealEstateEntity> getRealEstateEntityList(List<String> links, ExecutorService service) {
        List<RealEstateEntity> realEstateEntityList = new ArrayList<>();
        for (String link : links) {
            try (InputStream inputStream = ConnectionHelper.getUrlConnection(link).getInputStream()) {
                Future<List<RealEstateEntity>> realEstateFuture = service.submit(new RealEstateParser(inputStream));
                realEstateEntityList.addAll(realEstateFuture.get());
            } catch (InterruptedException | ExecutionException | IOException | NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
                log.warn("Ошибка {} при добавлении данных: {}", e.getClass().getName(), e.getMessage());
            }
        }
        return getRealEstateEntityListWithoutId(realEstateEntityList);
    }

    private List<RealEstateEntity> getRealEstateEntityListWithoutId(List<RealEstateEntity> realEstateEntityList) {
        return realEstateEntityList.stream().map(entity ->
                        RealEstateEntity.builder()
                                /*.addressReal(entity.getAddressReal())*/
                                .address(entity.getAddress())
                                .cadastralNumber(entity.getCadastralNumber())
                                .link(entity.getLink())
                                .lotName(entity.getLotName())
                                .lotNumber(entity.getLotNumber())
                                .minPrice(entity.getMinPrice())
                                .realEstateType(entity.getRealEstateType())
                                .publishDate(entity.getPublishDate())
                                .biddingStartTime(entity.getBiddingStartTime())
                                .biddingEndTime(entity.getBiddingEndTime())
                                .auctionStartDate(entity.getAuctionStartDate())
                                .build())
                .collect(Collectors.toList());
    }
}