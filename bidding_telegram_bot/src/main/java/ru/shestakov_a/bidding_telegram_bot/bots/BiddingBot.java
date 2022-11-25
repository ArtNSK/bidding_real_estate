package ru.shestakov_a.bidding_telegram_bot.bots;

import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import ru.shestakov_a.bidding_telegram_bot.handler.update.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс с инициализацией telegram бота.
 * Бот написан с испльзованем кода @see href<https://github.com/kobyzau/mjc-sandbox></https://github.com/kobyzau/mjc-sandbox>.
 * */
@Component
@Slf4j
public class BiddingBot extends TelegramLongPollingBot {

    @Value("${bot.bidding.username}")
    private String username;

    @Value("${bot.bidding.token}")
    private String token;

    @Autowired
    private List<UpdateHandler> updateHandlers;

    @PostConstruct
    public void init() {
        updateHandlers =
                updateHandlers.stream()
                        .sorted(Comparator.comparingInt(u -> u.getStage().getOrder()))
                        .collect(Collectors.toList());
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (Exception e) {
            log.error("Ошибка иницализации бота {}", e.getMessage());
        }
        setupCommands();
    }

    /**
     * Метод регистрирует пользовательские команды бота
     * */
    private void setupCommands() {
        try {
            List<BotCommand> commands =
                    Arrays.stream(Command.values())
                            .map(comm -> BotCommand.builder().command(comm.getName()).description(comm.getDesc()).build())
                            .collect(Collectors.toList());
            execute(SetMyCommands.builder().commands(commands).build());
        } catch (Exception e) {
            log.error("Ошибка при установке команд: {}", e.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        for (UpdateHandler updateHandler : updateHandlers) {
            try {
                if (updateHandler.handleUpdate(update)) {
                    return;
                }
            } catch (TelegramApiException e) {
                log.error("Ошибка в работе приложения: {}", e.getMessage());
            }
        }
    }
}
