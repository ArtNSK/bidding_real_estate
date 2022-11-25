package ru.shestakov_a.bidding_telegram_bot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Setter
@Getter
@Builder
public class MessageDataDTO<T> {
    private Long chatId;
    private Integer messageId;
    private String parseMode;
    private String text;
    private T replyMarkup;
}