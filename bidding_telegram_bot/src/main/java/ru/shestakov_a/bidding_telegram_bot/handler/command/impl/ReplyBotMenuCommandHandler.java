package ru.shestakov_a.bidding_telegram_bot.handler.command.impl;

import ru.shestakov_a.bidding_telegram_bot.bots.BiddingBot;
import ru.shestakov_a.bidding_telegram_bot.dto.MessageDataDTO;
import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import ru.shestakov_a.bidding_telegram_bot.entity.ReplyKeyboardButton;
import ru.shestakov_a.bidding_telegram_bot.handler.command.CommandHandler;
import ru.shestakov_a.bidding_telegram_bot.util.Constants;
import ru.shestakov_a.bidding_telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

/**
 * Реализация интрерфейса обработчика команды {@link Command#BOT_MENU }
 * */
@Component
public class ReplyBotMenuCommandHandler implements CommandHandler {
    @Autowired
    @Lazy
    private BiddingBot bot;

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        ReplyKeyboardMarkup replyKeyboardMarkup = prepareReplyKeyboard();
        MessageDataDTO messageDataDTO = MessageDataDTO.builder().
                replyMarkup(replyKeyboardMarkup)
                .chatId(message.getChatId())
                .parseMode(Constants.PARSE_HTML)
                .text(Constants.WELCOME_MESSAGE)
                .build();

        MessageUtil.sendMessage(bot, messageDataDTO);
    }

    @Override
    public Command getCommand() {
        return Command.BOT_MENU;
    }

    private ReplyKeyboardMarkup prepareReplyKeyboard() {
        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboardRow(
                        new KeyboardRow(
                                Arrays.asList(
                                        /*KeyboardButton.builder()
                                                .text(ReplyKeyboardButton.CITY.getLabel())
                                                .build(),*/
                                        KeyboardButton.builder()
                                                .text(ReplyKeyboardButton.ALL_CADASTRAL.getLabel())
                                                .build(),
                                        KeyboardButton.builder()
                                                .text(ReplyKeyboardButton.REGION.getLabel())
                                                .build()))
                )
                .keyboardRow(
                        new KeyboardRow(
                                Arrays.asList(
                                        KeyboardButton.builder()
                                                .text(ReplyKeyboardButton.CADASTRAL.getLabel())
                                                .build(),
                                        KeyboardButton.builder()
                                                .text(ReplyKeyboardButton.ABOUT.getLabel())
                                                .build())))
                .build();
    }
}