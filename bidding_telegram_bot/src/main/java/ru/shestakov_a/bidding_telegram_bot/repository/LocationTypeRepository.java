package ru.shestakov_a.bidding_telegram_bot.repository;

import ru.shestakov_a.bidding_telegram_bot.entity.db.LocationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс репозитория для сущности {@link LocationTypeEntity}
 * @author Shestakov Artem
 * */
@Repository
public interface LocationTypeRepository extends JpaRepository<LocationTypeEntity, Integer> {
    LocationTypeEntity findFirstByChatIdAndMessageIdOrderById(Long chatId, Integer messageId);

    void deleteByCreationTimeLessThan(Long creationTime);
}
