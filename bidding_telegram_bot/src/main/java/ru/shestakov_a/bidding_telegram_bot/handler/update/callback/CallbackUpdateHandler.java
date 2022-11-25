package ru.shestakov_a.bidding_telegram_bot.handler.update.callback;

import ru.shestakov_a.bidding_telegram_bot.dto.SerializableInlineObject;
import ru.shestakov_a.bidding_telegram_bot.dto.SerializableInlineType;
import ru.shestakov_a.bidding_telegram_bot.handler.update.UpdateHandler;
import ru.shestakov_a.bidding_telegram_bot.handler.update.UpdateHandlerStage;
import util.StringUtil;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

/**
 * Абстрактный класс с методами для получения Callback запросов из Update
 * */
public abstract class CallbackUpdateHandler<T extends SerializableInlineObject> implements UpdateHandler {

    protected abstract Class<T> getDtoType();

    protected abstract SerializableInlineType getSerializableType();

    protected abstract void handleCallback(Update update, T dto) throws TelegramApiException;

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery == null || callbackQuery.getMessage() == null) {
            return false;
        }
        String data = callbackQuery.getData();
        Optional<T> dto = StringUtil.deserialize(data, getDtoType());
        if(dto.isEmpty() || dto.get().getIndex() != getSerializableType().getIndex()) {
            return false;
        }
        handleCallback(update, dto.get());
        return true;
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.CALLBACK;
    }
}
