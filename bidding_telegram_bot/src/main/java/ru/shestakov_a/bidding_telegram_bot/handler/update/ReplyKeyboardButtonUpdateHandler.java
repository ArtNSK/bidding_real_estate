package ru.shestakov_a.bidding_telegram_bot.handler.update;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import real_estate.dto.bot.AuctionInformationDTO;
import real_estate.dto.controller.QuantityDTO;
import real_estate.dto.controller.RealEstateDTO;
import real_estate.dto.controller.RealEstateResponseDTO;
import real_estate.response.ResponseUtil;
import ru.shestakov_a.bidding_telegram_bot.bots.BiddingBot;
import ru.shestakov_a.bidding_telegram_bot.client.DbRestClient;
import ru.shestakov_a.bidding_telegram_bot.dto.*;
import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import ru.shestakov_a.bidding_telegram_bot.entity.ReplyKeyboardButton;
import ru.shestakov_a.bidding_telegram_bot.exception.EntityNotFoundException;
import ru.shestakov_a.bidding_telegram_bot.handler.command.CommandHandler;
import ru.shestakov_a.bidding_telegram_bot.handler.command.CommandHandlerFactory;
import ru.shestakov_a.bidding_telegram_bot.service.TemporaryDataService;
import ru.shestakov_a.bidding_telegram_bot.util.Constants;
import ru.shestakov_a.bidding_telegram_bot.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.shestakov_a.bidding_telegram_bot.handler.update.UpdateHandlerStage.REPLY_BUTTON;

/**
 * Реализация интерфейса {@link UpdateHandler} для обработки кнопок меню бота
 */
@Component
public class ReplyKeyboardButtonUpdateHandler implements UpdateHandler {

    @Autowired
    @Lazy
    private BiddingBot bot;

    @Autowired
    private CommandHandlerFactory commandHandlerFactory;

    @Autowired
    private DbRestClient dbRestClient;

    @Autowired
    private TemporaryDataService temporaryDataService;

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        if (!update.hasMessage()) {
            return false;
        }
        Message message = update.getMessage();

        if (!message.hasText()) {
            return false;
        }

        String messageText = message.getText();

        Optional<ReplyKeyboardButton> keyboardButton = ReplyKeyboardButton.parse(messageText);
        if (keyboardButton.isPresent()) {
            Long chatId = message.getChatId();
            selectButton(keyboardButton.get(), message, chatId);
            return true;
        }
        return false;
    }

    private void selectButton(ReplyKeyboardButton keyboardButton, Message message, Long chatId) throws TelegramApiException {
        switch (keyboardButton) {
            case CADASTRAL: {
                MessageDataDTO messageDataDTO = MessageDataDTO.builder()
                        .chatId(chatId)
                        .text(Constants.MESSAGE_SEND_CADASTRAL_NUMBER).
                        build();

                MessageUtil.sendMessage(bot, messageDataDTO);
            }
            break;

            case ABOUT: {
                CommandHandler commandHandler = commandHandlerFactory.getHandler(Command.ABOUT);
                commandHandler.handleCommand(message, Constants.EMPTY_MESSAGE);
            }
            break;

            /*case CITY:
                sendGeolocation(message, LocationType.CITY);
                break;*/

            case ALL_CADASTRAL: {
                sendMessageWithResponse(message);
            }
            break;


            case REGION:
                sendGeolocation(message, LocationType.REGION);
                break;
        }
    }

    @Override
    public UpdateHandlerStage getStage() {
        return REPLY_BUTTON;
    }

    private void sendGeolocation(Message message, LocationType locationType) throws TelegramApiException {
        int nextMessage = message.getMessageId() + 2;
        MessageIdDTO messageIdDto
                = new MessageIdDTO(message.getChatId(), nextMessage);

        temporaryDataService.setLocation(messageIdDto, locationType);

        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboardRow(new KeyboardRow(Collections.singletonList(KeyboardButton.builder()
                        .text(Constants.BUTTON_SEND_GEOLOCATION)
                        .requestLocation(true)
                        .build())))
                .build();

        MessageDataDTO messageDataDTO = MessageDataDTO.builder()
                .chatId(message.getChatId())
                .text(Constants.MESSAGE_SEND_GEOLOCATION)
                .replyMarkup(replyKeyboardMarkup)
                .build();

        MessageUtil.sendMessage(bot, messageDataDTO);
    }

    private void sendMessageWithResponse(Message message) throws TelegramApiException {
        Optional<RealEstateResponseDTO<RealEstateDTO>> response = dbRestClient.getAllRealEstateList(Constants.DISPLAY_LIMIT, Constants.CURSOR_INIT);

        if (response.isPresent()) {
            MessageIdDTO messageIdDTO = new MessageIdDTO(message.getChatId(), message.getMessageId());
            RealEstateResponseDTO<RealEstateDTO> responseDto = response.get();
            RealEstateDTO responseRealEstateDTO = responseDto.getItems().get(Constants.ONLY_RESPONSE);
            int quantity = getQuantity();
            int cursorInt = Integer.parseInt(responseDto.getCursor());
            int page = 1;

            PaginationControlValuesDTO paginationControlValuesDTO = new PaginationControlValuesDTO(Constants.CURSOR_INIT, quantity, page);
            List<InlineKeyboardButton> auctionInformation = prepareAuctionInformation(message.getChatId(),
                    message.getMessageId(), Integer.parseInt(paginationControlValuesDTO.getCursor()));
            AuctionInformationDTO auctionInformationDTO = getAuctionInformation(responseRealEstateDTO);

            setParamsForPagination(message, quantity, messageIdDTO, cursorInt, auctionInformationDTO);

            String responseString = ResponseUtil.getHtml(responseRealEstateDTO,
                    paginationControlValuesDTO.getPage(), paginationControlValuesDTO.getQuantity());
            List<InlineKeyboardButton> pagination = preparePagination(page,quantity, cursorInt);

            InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(pagination, auctionInformation);

            MessageDataDTO messageDataDTO = MessageDataDTO.builder()
                    .chatId(message.getChatId())
                    .text(responseString)
                    .replyMarkup(inlineKeyboardMarkup).build();

            MessageUtil.sendMessage(bot, messageDataDTO);

        }
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

    private AuctionInformationDTO getAuctionInformation(RealEstateDTO responseRealEstateDTO) {
        return AuctionInformationDTO.builder()
                .biddingStartTime(responseRealEstateDTO.getBiddingStartTime())
                .biddingEndTime(responseRealEstateDTO.getBiddingEndTime())
                .auctionStartDate(responseRealEstateDTO.getAuctionStartDate())
                .build();
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
                            SerializableInlineType.NEXT_ALL_OBJECTS_PAGE,
                            currentCursorInt,
                            cursorInt,
                            nextPage)))
                    .build());
        }
        return pagination;
    }

    private int getQuantity() {
        int quantity = Constants.QUANTITY_LIMIT;
        Optional<QuantityDTO> quantityDTO = dbRestClient.getRealEstateAllQuantity();
        if (quantityDTO.isPresent()) {
            quantity = Math.min(quantity, quantityDTO.get().getQuantity());
        }
        return quantity;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(List<InlineKeyboardButton> pagination,
                                                         List<InlineKeyboardButton> auctionInformation) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(pagination)
                .keyboardRow(auctionInformation)
                .build();
    }

    private void setParamsForPagination(Message message,
                                        int quantity,
                                        MessageIdDTO messageIdDTO,
                                        int cursorInt,
                                        AuctionInformationDTO auctionInformationDTO) {
        int nextMessageId = message.getMessageId() + 1;
        MessageIdDTO nextMessageIdDTO =
                new MessageIdDTO(message.getChatId(), nextMessageId);

        temporaryDataService.setQuantity(nextMessageIdDTO, quantity);
        temporaryDataService.setAuctionInformation(messageIdDTO, cursorInt, auctionInformationDTO);
    }
}