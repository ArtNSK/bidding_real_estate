package ru.shestakov_a.bidding_telegram_bot.handler.update;

import ru.shestakov_a.bidding_telegram_bot.dto.CommandDTO;
import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import ru.shestakov_a.bidding_telegram_bot.handler.command.CommandHandler;
import ru.shestakov_a.bidding_telegram_bot.handler.command.CommandHandlerFactory;
import ru.shestakov_a.bidding_telegram_bot.parser.CommandParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

/**
 * Реализация интерфейса {@link UpdateHandler} для обработки {@link Command}
 * */
@Component
public class CommandUpdateHandler implements UpdateHandler{

    @Autowired
    CommandParser commandParser;

    @Autowired
    CommandHandlerFactory commandHandlerFactory;

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        if (!update.hasMessage()) {
            return false;
        }
        Message message = update.getMessage();
        if (!message.hasText()) {
            return false;
        }
        String text = message.getText();
        Optional<CommandDTO> command = commandParser.parseCommand(text);
        if(!command.isPresent()) {
            return false;
        }
        handleCommand(update, command.get().getCommand(), text);

        return true;
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.COMMAND;
    }

    private void handleCommand(Update update, Command command, String text) throws TelegramApiException {
        CommandHandler commandHandler = commandHandlerFactory.getHandler(command);
        commandHandler.handleCommand(update.getMessage(), text);
    }
}