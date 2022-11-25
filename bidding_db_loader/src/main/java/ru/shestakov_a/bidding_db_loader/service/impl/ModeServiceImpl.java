package ru.shestakov_a.bidding_db_loader.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import real_estate.entity.RealEstateEntity;
import ru.shestakov_a.bidding_db_loader.controller.PropertiesHelper;
import ru.shestakov_a.bidding_db_loader.service.RealEstateService;
import ru.shestakov_a.bidding_db_loader.service.ModeService;

import java.util.List;

/**
 * Класс реализации интерфейса сервисного слоя для выбора конфигурации работы приложения
 * @author Shestakov Artem
 * */
@Service
@Slf4j
public class ModeServiceImpl implements ModeService {
    private static final String DATABASE_MODE = "realEstateServiceDbImpl";
    private static final String EXCEL_MODE = "realEstateServiceExcelImpl";
    private static final String DATABASE_SERVICE_MODE = "database";

    private String serviceMode;

    @Autowired
    private PropertiesHelper propertiesHelper;

    private RealEstateService realEstateService;

    @Override
    public void save(RealEstateEntity realEstateEntity) {
        realEstateService.save(realEstateEntity);
    }

    @Override
    public void saveAll(List<RealEstateEntity> realEstateEntities) {
        realEstateService.saveAll(realEstateEntities);
    }

    /**
     * В методе в зависимости от выбранного режим работы ({@link ModeServiceImpl#DATABASE_MODE}
     * либо {@link ModeServiceImpl#EXCEL_MODE}) выбирается подходящий бин для сервисва
     * {@link RealEstateService}
     * */
    @Autowired
    public void setRealEstateService(ApplicationContext context) {
        serviceMode = selectRealEstateServiceMode(propertiesHelper.getApplicationMode());
        log.info("Выбран режим работы: {}", serviceMode);
        realEstateService = (RealEstateService) context.getBean(serviceMode);
    }

    private String selectRealEstateServiceMode(String serviceMode) {
        if (serviceMode.equals(DATABASE_SERVICE_MODE)) {
            return DATABASE_MODE;
        } else {
            return EXCEL_MODE;
        }
    }
}
