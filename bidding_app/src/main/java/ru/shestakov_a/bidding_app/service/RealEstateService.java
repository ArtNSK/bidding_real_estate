package ru.shestakov_a.bidding_app.service;

import real_estate.dto.controller.*;
import real_estate.entity.RealEstateEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервисного слоя
 * @author Shestakov Artem
 * */
public interface RealEstateService {
    void saveAllRealEstate(List<RealEstateEntity> realEstateEntityList);

    RealEstateDTO getRealEstateById(String realEstateId);

    RealEstateResponseDTO<RealEstateDTO> getRealEstateList(int limit, Optional<String> cursor, Optional<String> cadastralNumber);

    RealEstateResponseDTO<RealEstateDTO> getRealEstateListByCadastralNumbers(int limit, Optional<String> cursor, List<String> cadastralNumberList);

    RealEstateResponseDTO<RealEstateDTO> getRealEstateListByCity(int limit, Optional<String> cursor, String region, String city);

    RealEstateResponseDTO<RealEstateDTO> getRealEstateListByRegion(int limit, Optional<String> cursor, String region);

    QuantityRealEstateCadastralNumberDTO getRealEstateQuantityByCadastralNumber(String cadastralNumber);

    QuantityRealEstateCityDTO getRealEstateQuantityByCity(String region, String city);

    QuantityRealEstateRegionDTO getRealEstateQuantityByRegion(String region);

    QuantityDTO getRealEstateAllQuantity();

    void deleteOldRealEstate(LocalDateTime date);

}