package ru.shestakov_a.bidding_db_loader.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import output.excel_worker.exception.IllegalFileExtensionException;
import real_estate.entity.RealEstateEntity;
import output.excel_worker.ExcelMapper;
import output.excel_worker.ExcelWriter;
import ru.shestakov_a.bidding_db_loader.service.RealEstateService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Класс реализации интерфейса сервисного слоя для сохранения данных в excel
 * @author Shestakov Artem
 * */
@Service
@Slf4j
public class RealEstateServiceExcelImpl implements RealEstateService {

    @Value("${excel.file.path}")
    private String excelFilePath;

    @Override
    public void save(RealEstateEntity realEstateEntity) {
        Optional<XSSFWorkbook> workbook =  ExcelMapper.getExcelWorkbook(Collections.singletonList(realEstateEntity));
        if (workbook.isPresent()) {
            try {
                ExcelWriter.writeExcelFile(workbook.get(), excelFilePath);
                log.info("Данные записаны в файл {}", excelFilePath);
            } catch (IllegalFileExtensionException e) {
                log.warn(e.getMessage());
            }
        } else {
            log.info("Нет данных для записи в файл");
        }
    }

    @Override
    public void saveAll(List<RealEstateEntity> realEstateEntities) {
        Optional<XSSFWorkbook> workbook = ExcelMapper.getExcelWorkbook(realEstateEntities);
        if (workbook.isPresent()) {
            try {
                ExcelWriter.writeExcelFile(workbook.get(), excelFilePath);
                log.info("Данные записаны в файл {}", excelFilePath);
            } catch (IllegalFileExtensionException e) {
                log.warn(e.getMessage());
            }
        } else {
            log.info("Нет данных для записи в файл");
        }
    }
}
