package ru.shestakov_a.bidding_telegram_bot.handler.command;

import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Интерфейс обработчика команд
 * */
public interface CommandHandler {

    void handleCommand(Message message, String text) throws TelegramApiException;

    Command getCommand();
}
