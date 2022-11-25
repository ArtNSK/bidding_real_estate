package ru.shestakov_a.bidding_telegram_bot.handler.update;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Интерфейс для работы с разными типами {@link Update}
 * */
public interface UpdateHandler {

    boolean handleUpdate(Update update) throws TelegramApiException;

    UpdateHandlerStage getStage();

}
