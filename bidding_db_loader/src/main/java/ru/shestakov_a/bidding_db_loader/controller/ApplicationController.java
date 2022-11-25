package ru.shestakov_a.bidding_db_loader.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import real_estate.entity.RealEstateEntity;
import real_estate.parser.RealEstateCounter;
import ru.shestakov_a.bidding_db_loader.service.DownloadService;
import ru.shestakov_a.bidding_db_loader.service.RealEstateParserService;
import ru.shestakov_a.bidding_db_loader.service.ModeService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс обрабатывает настройки работы из файла конфигурации приложения
 * @author Shestakov Artem
 * */
@Component
@Slf4j
public class ApplicationController {

    private final RealEstateParserService realEstateParserService;

    private final ModeService modeService;

    private final DownloadService downloadService;

    private final PropertiesHelper propertiesHelper;

    private static final String MODE_DOWNLOAD = "download";
    private static final String MODE_EXCEL = "excel";
    private static final String MODE_DATABASE = "database";


    @Autowired
    public ApplicationController(RealEstateParserService realEstateParserService,
                                 ModeService modeService,
                                 DownloadService downloadService,
                                 PropertiesHelper propertiesHelper
    ) {
        this.realEstateParserService = realEstateParserService;
        this.modeService = modeService;
        this.downloadService = downloadService;
        this.propertiesHelper = propertiesHelper;
        selectApplicationMode();
    }

    /**
     * Метод управляет работой приложения,
     * при выборе {@link ApplicationController#MODE_DOWNLOAD} происходит скачка JSON объектов,
     * при выборе {@link ApplicationController#MODE_DATABASE} происходит парсинг (из JSON) и загрузка
     * сущностей объектов недвижимости в базу данных,
     * при выборе {@link ApplicationController#MODE_EXCEL} происходит парсинг и вывод данных в файл excel
     * */
    private void selectApplicationMode() {
        if (propertiesHelper.getApplicationMode().equals(MODE_DOWNLOAD)) {
            try {
                downloadService.download();
            } catch (Exception e) {
                log.error("Ошибка {} при загрузке файлов: {}", e.getClass().getName(), e.getMessage());
            }
        } else if (propertiesHelper.getApplicationMode().equals(MODE_EXCEL)
                || propertiesHelper.getApplicationMode().equals(MODE_DATABASE)) {
            try {
                List<RealEstateEntity> realEstateEntities = realEstateParserService.getRealEstateList(propertiesHelper.getDirectoryName());
                modeService.saveAll(realEstateEntities);
                log.info("В базу данных сохранено {} сущностей", realEstateEntities.size());
            } catch (Exception e) {
                log.error("Ошибка {} при парсинге файлов: {}", e.getClass().getName(), e.getMessage());
            }
        }
    }
}
