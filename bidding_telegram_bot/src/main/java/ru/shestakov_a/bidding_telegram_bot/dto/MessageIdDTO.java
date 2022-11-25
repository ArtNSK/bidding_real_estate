package ru.shestakov_a.bidding_telegram_bot.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MessageIdDTO {
    private Long chatId;
    private Integer messageId;
}