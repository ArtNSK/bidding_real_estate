package ru.shestakov_a.bidding_telegram_bot.scheduler;

import ru.shestakov_a.bidding_telegram_bot.service.TemporaryDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Класс планировщика, который очищает базу данных в зависимости от актуальности данных
 * @author Shestakov Artem
 * */
@Service
@Slf4j
public class CleanerDb {

    @Autowired
    private TemporaryDataService temporaryDataService;

    @Scheduled(fixedDelay = 1800000)
    public void cleanDb() {
        long date = Instant.now().toEpochMilli() - 1800000L;
        temporaryDataService.deleteCadastralNumber(date);
        temporaryDataService.deleteLocation(date);
        temporaryDataService.deleteRegionCity(date);
        temporaryDataService.deleteQuantity(date);
        temporaryDataService.deleteCursor(date);
        temporaryDataService.deleteAuctionInformation(date);
    }

}
