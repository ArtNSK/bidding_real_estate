package ru.shestakov_a.bidding_telegram_bot.repository;

import ru.shestakov_a.bidding_telegram_bot.entity.db.AuctionInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс репозитория для сущности {@link AuctionInformationEntity}
 * @author Shestakov Artem
 * */
public interface AuctionInformationRepository extends JpaRepository<AuctionInformationEntity, Integer> {
    AuctionInformationEntity findFirstByChatIdAndMessageIdAndCursorOrderById(Long chatId, Integer messageId, Integer cursor);

    void deleteByCreationTimeLessThan(Long creationTime);
}