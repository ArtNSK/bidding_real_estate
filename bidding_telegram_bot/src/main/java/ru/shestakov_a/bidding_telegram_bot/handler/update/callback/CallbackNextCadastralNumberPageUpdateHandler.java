package ru.shestakov_a.bidding_telegram_bot.handler.update.callback;

import ru.shestakov_a.bidding_telegram_bot.dto.CallbackNextPageDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.MessageIdDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.PaginationControlValuesDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.SerializableInlineType;
import ru.shestakov_a.bidding_telegram_bot.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import real_estate.dto.controller.RealEstateDTO;
import real_estate.dto.controller.RealEstateResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс для обрабатки Callback запросов с пагинацией и выводом информации по запрашиваемому кадастровому номеру
 * */
@Component
@Slf4j
public class CallbackNextCadastralNumberPageUpdateHandler extends AbstractNextPageUpdateHandler {

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.NEXT_CADASTRAL_PAGE;
    }

    @Override
    protected void handleCallback(Update update, CallbackNextPageDTO dto) throws TelegramApiException {
        int previousCursor = dto.getPreviousCursor();
        int cursor = dto.getNextCursor();
        int page = dto.getPage();

        Message message = update.getCallbackQuery().getMessage();
        MessageIdDTO messageIdDto =
                new MessageIdDTO(message.getChatId(), message.getMessageId());

        String cadastralNumber = temporaryDataService.getCadastralNumber(messageIdDto);
        Integer quantity = temporaryDataService.getQuantity(messageIdDto);
        PaginationControlValuesDTO paginationControlValuesDTO = new PaginationControlValuesDTO(String.valueOf(cursor), quantity, page);

        if (!isMessageTimeout(cadastralNumber, quantity)) {
            Optional<RealEstateResponseDTO<RealEstateDTO>> response =
                    dbRestClient.getRealEstateList(Constants.DISPLAY_LIMIT, String.valueOf(cursor), cadastralNumber);

            handleResponse(response, message, messageIdDto, paginationControlValuesDTO, previousCursor);
        }
    }


    private boolean isMessageTimeout(String cadastralNumber, Integer quantity) {
        return cadastralNumber == null || quantity == null;
    }
}
