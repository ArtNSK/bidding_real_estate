package ru.shestakov_a.bidding_telegram_bot.parser;

import ru.shestakov_a.bidding_telegram_bot.dto.CommandDTO;

import java.util.Optional;

public interface CommandParser {

    Optional<CommandDTO> parseCommand(String message);
}