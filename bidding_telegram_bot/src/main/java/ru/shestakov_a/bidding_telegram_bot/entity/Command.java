package ru.shestakov_a.bidding_telegram_bot.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import util.StringUtil;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Command {
    REPLY_CADASTRAL_NUMBER("reply_number", "Возвращает кадастровые номера на запрос"),
    BOT_MENU("bot_menu", "Возможности бота"),
    ABOUT("about", "О боте");

    private final String name;
    private final String desc;

    public static Optional<Command> parseCommand(String command) {
        if (StringUtil.isBlank(command)) {
            return Optional.empty();
        }
        String formatName = StringUtil.trim(command).toLowerCase();
        return Stream.of(values()).filter(c -> c.name.equalsIgnoreCase(formatName)).findFirst();

    }
}
