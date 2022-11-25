package ru.shestakov_a.bidding_telegram_bot.handler.update;

import feign.FeignException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import real_estate.dto.bot.AuctionInformationDTO;
import real_estate.dto.controller.QuantityRealEstateRegionDTO;
import real_estate.dto.controller.RealEstateDTO;
import real_estate.dto.controller.RealEstateResponseDTO;
import real_estate.response.ResponseUtil;
import ru.shestakov_a.bidding_telegram_bot.bots.BiddingBot;
import ru.shestakov_a.bidding_telegram_bot.client.DadataClient;
import ru.shestakov_a.bidding_telegram_bot.client.DbRestClient;
import ru.shestakov_a.bidding_telegram_bot.dto.*;
import ru.shestakov_a.bidding_telegram_bot.dto.dadata_dto.AddressDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.dadata_dto.DataAddressDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.dadata_dto.SuggestionsDTO;
import ru.shestakov_a.bidding_telegram_bot.exception.DatabaseEntityTimeOverException;
import ru.shestakov_a.bidding_telegram_bot.service.TemporaryDataService;
import ru.shestakov_a.bidding_telegram_bot.util.Constants;
import ru.shestakov_a.bidding_telegram_bot.util.MessageUtil;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link UpdateHandler} для обработки получаемого геокодирования
 */
@Component
@Data
@Slf4j
public class GeolocationUpdateHandler implements UpdateHandler {

    @Value("${client.dadata.token}")
    String authHeader;

    @Autowired
    @Lazy
    private BiddingBot bot;

    @Autowired
    private DadataClient dadataClient;

    @Autowired
    private DbRestClient dbRestClient;


    @Autowired
    private TemporaryDataService temporaryDataService;

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException, DatabaseEntityTimeOverException {

        if (!update.hasMessage()) {
            return false;
        }
        Message message = update.getMessage();
        if (message.hasLocation()) {
            try {
                handleLocation(message);
            } catch (FeignException e) {
                e.printStackTrace();
                log.info("Нет данных для локации: {}", e.getMessage());
                sendMessageWithoutLocation(update);
            }
            return true;
        }
        return false;
    }

    /**
     * Метод обрабатывает сообщение с геолокацией. Отправляется запрос к серверу геокодирования, для
     * получения локации, затем отправляется запрос к серверу приложения торгов по недвижимости,
     * после отправляется ответ клиенту.
     */
    private void handleLocation(Message message) throws TelegramApiException {

        Location location = message.getLocation();

        SuggestionsDTO<AddressDTO> response = dadataClient.getAddress(authHeader,
                new GeolocationDTO(
                        location.getLatitude(),
                        location.getLongitude()));
        DataAddressDTO dataAddressDto = response.getSuggestions().get(0).getData();
        String region = dataAddressDto.getRegion();
        String city = dataAddressDto.getCity();

        /*String region = "санкт-петербург";
        String city = "санкт-петербург";*/

        MessageIdDTO messageIdDTOLocation =
                new MessageIdDTO(message.getChatId(), message.getMessageId());

//        boolean locationTypeCity = isCity(messageIdDTOLocation); //возможно выбирать локацию как по региону так и по городу, сейчас отключено
        boolean locationTypeCity = false;

        Optional<RealEstateResponseDTO<RealEstateDTO>> responseFromDb = getResponseFromDb(locationTypeCity, region, city);

        if (responseFromDb.isPresent()) {
            sendMessage(message, responseFromDb, region, city, locationTypeCity, messageIdDTOLocation);
        }
    }

    private void sendMessageWithoutLocation(Update update) throws TelegramApiException {
        MessageDataDTO messageDataDTO = MessageDataDTO.builder()
                .chatId(update.getMessage().getChatId())
                .text(Constants.MESSAGE_LOCATION_NOT_FOUND)
                .build();
        MessageUtil.sendMessage(bot, messageDataDTO);
    }

    private void sendMessage(Message message,
                             Optional<RealEstateResponseDTO<RealEstateDTO>> responseFromDb,
                             String region,
                             String city,
                             boolean locationTypeCity,
                             MessageIdDTO messageIdDTOLocation) throws TelegramApiException {
        RealEstateResponseDTO<RealEstateDTO> responseDto = responseFromDb.get();
        RealEstateDTO responseRealEstateDTO = responseDto.getItems().get(Constants.ONLY_RESPONSE);
        int cursorInt = Integer.parseInt(responseDto.getCursor());
        int quantity = getQuantity(locationTypeCity, region, city);
        int page = 1;
        String responseString = ResponseUtil.getHtml(responseRealEstateDTO, page, quantity);
        AuctionInformationDTO auctionInformationDTO = getAuctionInformation(responseRealEstateDTO);

        setParamsForPagination(message, region, city, quantity, messageIdDTOLocation, cursorInt, auctionInformationDTO);

        List<InlineKeyboardButton> pagination = preparePagination(page, quantity, cursorInt);
        List<InlineKeyboardButton> auctionInformation = prepareAuctionInformation(
                message.getChatId(), message.getMessageId(), cursorInt);

        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(pagination, auctionInformation);

        MessageDataDTO messageDataDTO = MessageDataDTO.builder()
                .chatId(message.getChatId())
                .text(responseString)
                .replyMarkup(inlineKeyboardMarkup).build();

        MessageUtil.sendMessage(bot, messageDataDTO);
    }

    private void setParamsForPagination(Message message,
                                        String region,
                                        String city,
                                        int quantity,
                                        MessageIdDTO messageIdDTOLocation,
                                        int cursorInt,
                                        AuctionInformationDTO auctionInformationDTO) {
        int nextMessageId = message.getMessageId() + 1;
        MessageIdDTO messageIdDto =
                new MessageIdDTO(message.getChatId(), nextMessageId);

        temporaryDataService.setRegionAndCity(messageIdDto, region, city);
        temporaryDataService.setQuantity(messageIdDto, quantity);
        temporaryDataService.setAuctionInformation(messageIdDTOLocation, cursorInt, auctionInformationDTO);
    }

    private AuctionInformationDTO getAuctionInformation(RealEstateDTO responseRealEstateDTO) {
        return AuctionInformationDTO.builder()
                .biddingStartTime(responseRealEstateDTO.getBiddingStartTime())
                .biddingEndTime(responseRealEstateDTO.getBiddingEndTime())
                .auctionStartDate(responseRealEstateDTO.getAuctionStartDate())
                .build();
    }

    /*private boolean isCity(MessageIdDTO messageIdDTO) {
        int locationType = temporaryDataService.getLocation(messageIdDTO);
        return locationType == LocationType.CITY.getIndex();
    }*/

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.LOCATION;
    }

    private List<InlineKeyboardButton> preparePagination(
            int page,
            int quantity,
            int cursorInt) {
        List<InlineKeyboardButton> pagination = new ArrayList<>();
        int nextPage = page + 1;
        int currentCursorInt = Integer.parseInt(Constants.CURSOR_INIT);
        if (page < quantity) {
            pagination.add(
                    InlineKeyboardButton.builder()
                            .text(Constants.BUTTON_LEFT)
                            .callbackData(StringUtil.serialize(new AlertDTO(Constants.MESSAGE_FIRST_PAGE, false)))
                            .build()
            );
            pagination.add(InlineKeyboardButton.builder()
                    .text(Constants.BUTTON_RIGHT)
                    .callbackData(StringUtil.serialize(new CallbackNextPageDTO(
                            SerializableInlineType.NEXT_GEOLOCATION_PAGE,
                            currentCursorInt,
                            cursorInt,
                            nextPage)))
                    .build());
        }
        return pagination;
    }

    private List<InlineKeyboardButton> prepareAuctionInformation(
            Long chatId,
            Integer messageId,
            Integer cursorInt) {
        return Collections.singletonList(InlineKeyboardButton.builder()
                .text("Информация об аукционе")
                .callbackData(StringUtil.serialize(new AlertAuctionInformationDTO(chatId, messageId, cursorInt)))
                .build());
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(List<InlineKeyboardButton> pagination,
                                                         List<InlineKeyboardButton> auctionInformation) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(pagination)
                .keyboardRow(auctionInformation)
                .build();
    }

    private Optional<RealEstateResponseDTO<RealEstateDTO>> getResponseFromDb(boolean isCity, String region, String city) {
        return /*isCity ? dbRestClient.getRealEstateListByCity(
                Constants.DISPLAY_LIMIT, Constants.CURSOR_INIT, region, city) :*/
                dbRestClient.getRealEstateListByRegion(
                        Constants.DISPLAY_LIMIT, Constants.CURSOR_INIT, region);
    }

    private int getQuantity(boolean isCity, String region, String city) {
        int quantity = 0;
        /*if (isCity) {
            Optional<QuantityRealEstateCityDTO> countCItyDto = dbRestClient.getRealEstateQuantityByCity(region, city);
            if (countCItyDto.isPresent()) {
                quantity = countCItyDto.get().getQuantity();
            }
        } else {*/
        Optional<QuantityRealEstateRegionDTO> countRegionDto = dbRestClient.getRealEstateQuantityByRegion(region);
        if (countRegionDto.isPresent()) {
            quantity = countRegionDto.get().getQuantity();
        }
//        }
        return quantity;
    }
}