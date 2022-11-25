package ru.shestakov_a.bidding_db_loader.service;


import real_estate.entity.RealEstateEntity;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс сервисного слоя для парсинга данных из JSON объектов сохраненных в файлы
 * @author Shestakov Artem
 * */
public interface RealEstateParserService {
    List<RealEstateEntity> getRealEstateList(String directoryName) throws IOException;
//    List<RealEstateEntity> getRealEstateFromLot(String fileName) throws IOException;
}
