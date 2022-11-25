package ru.shestakov_a.bidding_telegram_bot.exception;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
public class DatabaseEntityTimeOverException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Время ожидания ответа истекло";
    private Long chatId;

    public DatabaseEntityTimeOverException(String message) {
        super(message);
    }

    public DatabaseEntityTimeOverException(){
        super(DEFAULT_MESSAGE);
    }
}
