package ru.shestakov_a.bidding_telegram_bot.util;

import ru.shestakov_a.bidding_telegram_bot.dto.MessageDataDTO;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилитный класс для отправки сообщений бота клиенту
 * */
@UtilityClass
public class MessageUtil {
    private final static Pattern CADASTRAL_NUMBER_PATTERN = Pattern.compile("[0-9., ]+(?:[:;]+[0-9., ]+){2,}");

    public Optional<String> parseCadastralNumber(String cadastralNumber) {
        Matcher matcher = CADASTRAL_NUMBER_PATTERN.matcher(cadastralNumber);
        return matcher.find() ? Optional.of(matcher.group()) :
                Optional.empty();
    }

    public void sendMessage(TelegramLongPollingBot bot, MessageDataDTO<ReplyKeyboard> messageDataDTO) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(messageDataDTO.getChatId()))
                .text(messageDataDTO.getText())
                .parseMode(Constants.PARSE_HTML)
                .replyMarkup(messageDataDTO.getReplyMarkup())
                .build());
    }

    public void editMessage(TelegramLongPollingBot bot, MessageDataDTO<InlineKeyboardMarkup> messageDataDTO) throws TelegramApiException {
        bot.execute(
                EditMessageText.builder()
                        .messageId(messageDataDTO.getMessageId())
                        .chatId(String.valueOf(messageDataDTO.getChatId()))
                        .text(messageDataDTO.getText())
                        .parseMode(Constants.PARSE_HTML)
                        .replyMarkup(messageDataDTO.getReplyMarkup())
                        .build());
    }

    public MessageDataDTO getMessageData(Long chatId, String text, ReplyKeyboardMarkup replyKeyboardMarkup) {
        return MessageDataDTO.builder()
                .text(text)
                .replyMarkup(replyKeyboardMarkup)
                .parseMode(Constants.PARSE_HTML)
                .chatId(chatId)
                .build();
    }
}