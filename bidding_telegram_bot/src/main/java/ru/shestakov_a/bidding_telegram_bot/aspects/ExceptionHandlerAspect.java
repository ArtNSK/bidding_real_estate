package ru.shestakov_a.bidding_telegram_bot.aspects;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.shestakov_a.bidding_telegram_bot.bots.BiddingBot;
import ru.shestakov_a.bidding_telegram_bot.dto.MessageDataDTO;
import ru.shestakov_a.bidding_telegram_bot.exception.DatabaseEntityTimeOverException;
import ru.shestakov_a.bidding_telegram_bot.util.Constants;
import ru.shestakov_a.bidding_telegram_bot.util.MessageUtil;


@Component
@Aspect
@Slf4j
public class ExceptionHandlerAspect {

    @Autowired
    @Lazy
    private BiddingBot bot;

    @AfterThrowing(pointcut = "execution(public * ru.shestakov_a.bidding_telegram_bot.service.TemporaryDataServiceImpl.get*(..)), ",
            throwing="e")
    public void doRecoveryActions(DatabaseEntityTimeOverException e) throws TelegramApiException {
            log.warn("Ошибка работы сервиса: {}", e.getMessage());
            MessageDataDTO messageDataDTO = MessageUtil.getMessageData(
                    e.getChatId(),
                    Constants.MESSAGE_UPDATE_BOT,
                    ReplyKeyboardMarkup.builder().build());
            MessageUtil.sendMessage(bot, messageDataDTO);
    }
}