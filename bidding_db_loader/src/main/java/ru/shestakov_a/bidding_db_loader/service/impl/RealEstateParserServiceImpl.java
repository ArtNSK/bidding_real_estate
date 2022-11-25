package ru.shestakov_a.bidding_db_loader.service.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import real_estate.entity.RealEstateEntity;
import real_estate.parser.RealEstateParser;
import ru.shestakov_a.bidding_db_loader.service.RealEstateParserService;
import util.DownloaderJsonsHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Класс реализации интерфейса сервисного слоя для парсинга сущностей из JSON объектов
 * @author Shestakov Artem
 * */
@Slf4j
@Service
@Getter
@Setter
public class RealEstateParserServiceImpl implements RealEstateParserService {
    private final static int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * @param directoryName Директория с JSON объектами
     * @return Список {@link RealEstateEntity} сущностей объектов недвижимости
     * */
    @Override
    public List<RealEstateEntity> getRealEstateList(String directoryName) throws IOException {
        List<String> fileNameList = DownloaderJsonsHelper.getFileNameList(directoryName);

        ExecutorService service = Executors.newFixedThreadPool(AVAILABLE_PROCESSORS);
        List<RealEstateEntity> realEstateEntityList = parseFiles(fileNameList, service);
        service.shutdown();

        return realEstateEntityList;
    }

    /**
     * Метод парсит сохраненные в файлы JSON объекты
     * @param fileNameList Список считываемых файлов
     * @param service Сервис реализующий многопоточный парсинг файлов
     * @return Список {@link RealEstateEntity} сущностей объектов недвижимости
     * */
    private List<RealEstateEntity> parseFiles(List<String> fileNameList, ExecutorService service) throws IOException {
        List<RealEstateEntity> realEstateEntityList = new ArrayList<>();
        for (String fileName : fileNameList) {
            try(InputStream inputStream = new FileInputStream(fileName)) {
                Future<List<RealEstateEntity>> realEstateFuture = service.submit(new RealEstateParser(inputStream));
                realEstateEntityList.addAll(realEstateFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                log.warn("Ошибка {} при добавлении данных: {}", e.getClass().getName(), e.getMessage());
            }
        }
        return realEstateEntityList;
    }
}
