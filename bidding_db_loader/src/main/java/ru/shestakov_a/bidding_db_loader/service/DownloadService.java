package ru.shestakov_a.bidding_db_loader.service;

/**
 * Интерфейс сервисного слоя для загрузки JSON объектов
 * @author Shestakov Artem
 * */
public interface DownloadService {
    void download() throws Exception;
}
