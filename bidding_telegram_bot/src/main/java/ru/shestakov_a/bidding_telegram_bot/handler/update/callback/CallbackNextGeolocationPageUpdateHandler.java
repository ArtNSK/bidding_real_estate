package ru.shestakov_a.bidding_telegram_bot.handler.update.callback;

import ru.shestakov_a.bidding_telegram_bot.dto.CallbackNextPageDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.MessageIdDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.PaginationControlValuesDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.SerializableInlineType;
import ru.shestakov_a.bidding_telegram_bot.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.utils.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import real_estate.dto.controller.RealEstateDTO;
import real_estate.dto.controller.RealEstateResponseDTO;

import java.util.*;

/**
 * Класс для обрабатки Callback запросов с пагинацией и выводом информации по запрашиваемой локации
 * */
@Component
@Slf4j
public class CallbackNextGeolocationPageUpdateHandler extends AbstractNextPageUpdateHandler {

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.NEXT_GEOLOCATION_PAGE;
    }

    @Override
    protected void handleCallback(Update update, CallbackNextPageDTO dto) throws TelegramApiException {
        int previousCursor = dto.getPreviousCursor();
        int cursor = dto.getNextCursor();
        int page = dto.getPage();

        Message message = update.getCallbackQuery().getMessage();
        MessageIdDTO messageIdDto =
                new MessageIdDTO(message.getChatId(), message.getMessageId());

        Pair<String, String> regionAndCity = temporaryDataService.getRegionAndCity(messageIdDto);
        Integer quantity = temporaryDataService.getQuantity(messageIdDto);
        PaginationControlValuesDTO paginationControlValuesDTO = new PaginationControlValuesDTO(String.valueOf(cursor), quantity, page);


        if (!isMessageTimeout(regionAndCity, quantity)) {
            String region = regionAndCity.getFirst();
            String city = regionAndCity.getSecond();
            Optional<RealEstateResponseDTO<RealEstateDTO>> response =
                    getResponseFromDb(region, city, String.valueOf(cursor));

            handleResponse(response, message, messageIdDto, paginationControlValuesDTO, previousCursor);
        }
    }

    private boolean isMessageTimeout(Pair<String, String> regionCity, Integer quantity) {
        return regionCity == null || quantity == null;
    }

    private Optional<RealEstateResponseDTO<RealEstateDTO>> getResponseFromDb(String region, String city, String cursor) {
        return /*city != null ?
                dbRestClient.getRealEstateListByCity(Constants.DISPLAY_LIMIT, cursor, region, city)
                :*/
                dbRestClient.getRealEstateListByRegion(Constants.DISPLAY_LIMIT, cursor, region);
    }

}
