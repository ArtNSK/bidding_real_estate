package ru.shestakov_a.bidding_telegram_bot.parser;

import ru.shestakov_a.bidding_telegram_bot.dto.CommandDTO;
import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.glassfish.grizzly.utils.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Реализация интерфейса с методами для парсинга команд {@link Command}
 * */
@Component
@NoArgsConstructor
@AllArgsConstructor
public class BaseCommandParser implements CommandParser {
    private final String PREFIX_FOR_COMMAND = "/";
    private final String DELIMITER_COMMAND_BOTNAME = "@";
    private final String SPACE = " ";
    private final String EMPTY_SYMBOL = "";

    @Value("${bot.bidding.username}")
    private String botName;

    @Override
    public Optional<CommandDTO> parseCommand(String message) {
        if (StringUtil.isBlank(message)) {
            return Optional.empty();
        }
        String trimText = StringUtil.trim(message);
        Pair<String, String> commandsAndText = getDelimitedCommandFromText(trimText);
        if (isCommand(commandsAndText.getFirst())) {
            if (isCommandForMe(commandsAndText.getFirst())) {
                String commandForParse = cutCommandFromFullText(commandsAndText.getFirst());
                Optional<Command> command = Command.parseCommand(commandForParse);
                return command.map(comm -> new CommandDTO(comm, commandsAndText.getSecond()));
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private String cutCommandFromFullText(String text) {
        return text.contains(DELIMITER_COMMAND_BOTNAME)
                ? text.substring(1, text.indexOf(DELIMITER_COMMAND_BOTNAME))
                : text.substring(1);
    }

    private Pair<String, String> getDelimitedCommandFromText(String trimText) {
        Pair<String, String> commandText;

        if (trimText.contains(SPACE)) {
            int indexOfSpace = trimText.indexOf(SPACE);
            commandText = new Pair<>(trimText.substring(0, indexOfSpace), trimText.substring(indexOfSpace + 1));
        } else {
            commandText = new Pair<>(trimText, EMPTY_SYMBOL);
        }
        return commandText;
    }

    private boolean isCommand(String text) {
        return text.startsWith(PREFIX_FOR_COMMAND);
    }

    private boolean isCommandForMe(String command) {
        if (command.contains(DELIMITER_COMMAND_BOTNAME)) {
            String botNameForEqual = command.substring(command.indexOf(DELIMITER_COMMAND_BOTNAME) + 1);
            return botName.equals(botNameForEqual);
        }
        return true;
    }
}
