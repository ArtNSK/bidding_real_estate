package ru.shestakov_a.bidding_db_loader.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import real_estate.entity.RealEstateEntity;
import ru.shestakov_a.bidding_db_loader.repository.RealEstateRepository;
import ru.shestakov_a.bidding_db_loader.service.RealEstateService;

import java.util.List;

/**
 * Класс реализации интерфейса сервисного слоя для работы с базой данных
 * @author Shestakov Artem
 * */
@Service
@RequiredArgsConstructor
public class RealEstateServiceDbImpl implements RealEstateService {

    @Autowired
    private RealEstateRepository realEstateRepository;

    @Override
    public void save(RealEstateEntity realEstateEntity) {
        realEstateRepository.save(realEstateEntity);
    }

    @Override
    public void saveAll(List<RealEstateEntity> realEstateEntities) {
        realEstateRepository.saveAll(realEstateEntities);
    }
}
