package ru.shestakov_a.bidding_telegram_bot.handler.command.impl;


import ru.shestakov_a.bidding_telegram_bot.bots.BiddingBot;
import ru.shestakov_a.bidding_telegram_bot.dto.MessageDataDTO;
import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import ru.shestakov_a.bidding_telegram_bot.handler.command.CommandHandler;
import ru.shestakov_a.bidding_telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Реализация интрерфейса обработчика команды {@link Command#ABOUT }
 * */
@Component
public class AboutCommandHandler implements CommandHandler {

  @Autowired @Lazy
  private BiddingBot bot;

  @Override
  public void handleCommand(Message message, String text) throws TelegramApiException {
    String messageText = "Этот бот позволяет найти торги по актуальным лотам недвижимости поблизости";

    MessageDataDTO messageDataDTO = MessageDataDTO.builder().text(messageText).chatId(message.getChatId())
            .replyMarkup(ReplyKeyboardMarkup.builder().build()).build();

    MessageUtil.sendMessage(bot, messageDataDTO);
  }

  @Override
  public Command getCommand() {
    return Command.ABOUT;
  }
}
