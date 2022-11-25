package ru.shestakov_a.bidding_telegram_bot.repository;

import ru.shestakov_a.bidding_telegram_bot.entity.db.CursorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс репозитория для сущности {@link CursorEntity}
 * @author Shestakov Artem
 * */
@Repository
public interface CursorRepository extends JpaRepository<CursorEntity, Integer> {
    CursorEntity findFirstByChatIdAndMessageIdAndCursorOrderById(Long chatId, Integer messageId, Integer cursor);

    void  deleteByCreationTimeLessThan(Long creationTime);
}
