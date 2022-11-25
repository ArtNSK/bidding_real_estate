package ru.shestakov_a.bidding_telegram_bot.handler.update.callback;

import ru.shestakov_a.bidding_telegram_bot.bots.BiddingBot;
import ru.shestakov_a.bidding_telegram_bot.dto.AlertDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.SerializableInlineType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Класс для отправки Alert сообщений при соответствующем Callback запросе
 * */
@Component
public class AlertUpdateHandler extends CallbackUpdateHandler<AlertDTO> {
    @Autowired
    @Lazy
    private BiddingBot bot;

    @Override
    protected Class<AlertDTO> getDtoType() {
        return AlertDTO.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.ALERT;
    }

    @Override
    protected void handleCallback(Update update, AlertDTO dto) throws TelegramApiException {
        bot.execute(
                AnswerCallbackQuery.builder()
                        .cacheTime(10)
                        .text(dto.getMessage())
                        .showAlert(dto.isFullMode())
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .build()
        );
    }
}
