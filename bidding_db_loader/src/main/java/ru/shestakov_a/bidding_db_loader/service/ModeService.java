package ru.shestakov_a.bidding_db_loader.service;

import real_estate.entity.RealEstateEntity;

import java.util.List;

/**
 * Интерфейс сервисного слоя для выбора конфигурации работы приложения
 * @author Shestakov Artem
 * */
public interface ModeService {
    void save(RealEstateEntity realEstateEntity);

    void saveAll(List<RealEstateEntity> realEstateEntities);

}
