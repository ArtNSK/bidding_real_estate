package ru.shestakov_a.bidding_telegram_bot.dto;

import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CommandDTO {
    private final Command command;
    private final String text;
}