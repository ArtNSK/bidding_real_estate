package ru.shestakov_a.bidding_telegram_bot.repository;

import ru.shestakov_a.bidding_telegram_bot.entity.db.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс репозитория для сущности {@link RegionEntity}
 * @author Shestakov Artem
 * */
@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer> {
    RegionEntity findFirstByChatIdAndMessageIdOrderById(Long chatId, Integer messageId);

    void deleteByCreationTimeLessThan(Long creationTime);
}
