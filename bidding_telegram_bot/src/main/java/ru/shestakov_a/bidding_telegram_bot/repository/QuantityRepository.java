package ru.shestakov_a.bidding_telegram_bot.repository;

import ru.shestakov_a.bidding_telegram_bot.entity.db.QuantityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс репозитория для сущности {@link QuantityEntity}
 * @author Shestakov Artem
 * */
@Repository
public interface QuantityRepository extends JpaRepository<QuantityEntity, Integer> {
    QuantityEntity findFirstByChatIdAndMessageIdOrderById(Long chatId, Integer messageId);

    void deleteByCreationTimeLessThan(Long creationTime);
}
