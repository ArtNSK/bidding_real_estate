package ru.shestakov_a.bidding_telegram_bot.repository;

import ru.shestakov_a.bidding_telegram_bot.entity.db.CadastralNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс репозитория для сущности {@link CadastralNumberEntity}
 * @author Shestakov Artem
 * */
@Repository
public interface CadastralNumberRepository extends JpaRepository<CadastralNumberEntity, Integer> {
    CadastralNumberEntity findFirstByChatIdAndMessageIdOrderById(Long chatId, Integer messageId);

    void deleteByCreationTimeLessThan(Long creationTime);

}
