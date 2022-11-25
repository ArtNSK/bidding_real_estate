package ru.shestakov_a.bidding_telegram_bot.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityNotFoundException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Сущность не найдена";
    private Long chatId;

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(){
        super(DEFAULT_MESSAGE);
    }
}
