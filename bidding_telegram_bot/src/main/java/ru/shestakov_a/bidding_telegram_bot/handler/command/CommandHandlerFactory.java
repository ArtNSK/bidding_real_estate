package ru.shestakov_a.bidding_telegram_bot.handler.command;

import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Фабричный метод для интерфейса {@link CommandHandler}
 * */
@Component
public class CommandHandlerFactory {

    @Autowired private List<CommandHandler> handlers;
    private Map<Command, CommandHandler> map;


    @PostConstruct
    private void init() {
        map = new HashMap<>();
        handlers.forEach(hand -> map.put(hand.getCommand(), hand));
    }

    public CommandHandler getHandler(Command command) {
        return Optional.ofNullable(map.get(command))
                .orElseThrow(() -> new IllegalStateException("Not supported command: " + command));
    }
}