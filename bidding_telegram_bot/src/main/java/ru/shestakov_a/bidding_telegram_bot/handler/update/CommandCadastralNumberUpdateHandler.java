package ru.shestakov_a.bidding_telegram_bot.handler.update;

import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import ru.shestakov_a.bidding_telegram_bot.handler.command.CommandHandler;
import ru.shestakov_a.bidding_telegram_bot.handler.command.CommandHandlerFactory;
import ru.shestakov_a.bidding_telegram_bot.util.MessageUtil;
import util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

/**
 * Реализация интерфейса {@link UpdateHandler} для обработки {@link Command#REPLY_CADASTRAL_NUMBER}
 * */
@Component
public class CommandCadastralNumberUpdateHandler implements UpdateHandler{

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
        String messageText = message.getText();
        Optional<String> cadastralNumber = MessageUtil.parseCadastralNumber(messageText);

        if (cadastralNumber.isPresent()) {
            String prepareCadastralNumber = StringUtil.trim(cadastralNumber.get());
            CommandHandler commandHandler = commandHandlerFactory.getHandler(Command.REPLY_CADASTRAL_NUMBER);
            commandHandler.handleCommand(message, prepareCadastralNumber);
            return true;
        }
        return false;
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.COMMAND_CADASTRAL_NUMBER;
    }


}
