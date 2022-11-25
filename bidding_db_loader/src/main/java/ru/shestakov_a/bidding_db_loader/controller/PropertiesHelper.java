package ru.shestakov_a.bidding_db_loader.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Класс считывает настройки работы из файла конфигурации приложения
 * @author Shestakov Artem
* */
@Component
@Data
public class PropertiesHelper {
    @Value("${directory.name}")
    private String directoryName;

    @Value("${excel.file.path}")
    private String excelFilePath;

    @Value("${json.download.link}")
    private String jsonDownloadLink;

    @Value("${application.mode}")
    private String applicationMode;
}
