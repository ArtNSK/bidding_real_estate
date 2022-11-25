package ru.shestakov_a.bidding_telegram_bot.handler.update.callback;

import ru.shestakov_a.bidding_telegram_bot.bots.BiddingBot;
import ru.shestakov_a.bidding_telegram_bot.client.DbRestClient;
import ru.shestakov_a.bidding_telegram_bot.dto.*;
import ru.shestakov_a.bidding_telegram_bot.service.TemporaryDataService;
import ru.shestakov_a.bidding_telegram_bot.util.Constants;
import ru.shestakov_a.bidding_telegram_bot.util.MessageUtil;
import util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import real_estate.dto.bot.AuctionInformationDTO;
import real_estate.dto.controller.RealEstateDTO;
import real_estate.dto.controller.RealEstateResponseDTO;
import real_estate.response.ResponseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Абстрактный класс с методами для обработки пагинации страниц при Callback запросе
 */
public abstract class AbstractNextPageUpdateHandler extends CallbackUpdateHandler<CallbackNextPageDTO> {
    @Override
    protected Class<CallbackNextPageDTO> getDtoType() {
        return CallbackNextPageDTO.class;
    }

    @Autowired
    @Lazy
    protected BiddingBot bot;

    @Autowired
    protected DbRestClient dbRestClient;

    @Autowired
    protected TemporaryDataService temporaryDataService;

    protected void editMessage(Message message,
                               Optional<RealEstateResponseDTO<RealEstateDTO>> response,
                               List<InlineKeyboardButton> pagination,
                               MessageIdDTO messageIdDto,
                               PaginationControlValuesDTO paginationControlValuesDTO,
                               SerializableInlineType serializableInlineType) throws TelegramApiException {
        if (response.isPresent()) {
            RealEstateResponseDTO<RealEstateDTO> responseDto = response.get();
            RealEstateDTO responseRealEstateDTO = responseDto.getItems().get(Constants.ONLY_RESPONSE);
            AuctionInformationDTO auctionInformationDTO = getAuctionInformation(responseRealEstateDTO);
            temporaryDataService.setAuctionInformation(messageIdDto, Integer.parseInt(paginationControlValuesDTO.getCursor()), auctionInformationDTO);

            String responseString = ResponseUtil.getHtml(responseRealEstateDTO,
                    paginationControlValuesDTO.getPage(), paginationControlValuesDTO.getQuantity());
            int nextCursorInt = Integer.parseInt(responseDto.getCursor());
            pagination.addAll(
                    prepareRightButtonForPagination(paginationControlValuesDTO.getPage(),
                            paginationControlValuesDTO.getQuantity(),
                            Integer.parseInt(paginationControlValuesDTO.getCursor()),
                            nextCursorInt,
                            serializableInlineType,
                            responseRealEstateDTO));

            List<InlineKeyboardButton> auctionInformation = prepareAuctionInformation(
                    message.getChatId(),
                    message.getMessageId(),
                    Integer.parseInt(paginationControlValuesDTO.getCursor()));


            InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(pagination, auctionInformation);
            MessageDataDTO messageDataDTO = MessageDataDTO.builder()
                    .messageId(message.getMessageId())
                    .chatId(message.getChatId())
                    .text(responseString)
                    .replyMarkup(inlineKeyboardMarkup).build();

            MessageUtil.editMessage(bot, messageDataDTO);
        } else {
            System.out.println("response is no present" );
        }
    }

    protected AuctionInformationDTO getAuctionInformation(RealEstateDTO responseRealEstateDTO) {
        return AuctionInformationDTO.builder()
                .biddingStartTime(responseRealEstateDTO.getBiddingStartTime())
                .biddingEndTime(responseRealEstateDTO.getBiddingEndTime())
                .auctionStartDate(responseRealEstateDTO.getAuctionStartDate())
                .build();
    }

    protected List<InlineKeyboardButton> prepareRightButtonForPagination(
            int page,
            int quantity,
            int cursor,
            int nextCursorInt,
            SerializableInlineType serializableInlineType,
            RealEstateDTO responseRealEstateDTO) {
        List<InlineKeyboardButton> pagination = new ArrayList<>();
        int nextPage = page + 1;
        System.out.println("cursor - " + cursor + ", nextCursorInt - " + nextCursorInt);

        if (page >= quantity || cursor >= nextCursorInt) {
            pagination.add(InlineKeyboardButton.builder()
                    .text(Constants.BUTTON_RIGHT)
                    .callbackData(StringUtil.serialize(new AlertDTO(Constants.MESSAGE_LAST_PAGE, false)))
                    .build());
        } else {
            pagination.add(InlineKeyboardButton.builder()
                    .text(Constants.BUTTON_RIGHT)
                    .callbackData(StringUtil.serialize(
                            /*new CallbackNextPageDTO(
                                    SerializableInlineType.NEXT_GEOLOCATION_PAGE,
                                    cursor,
                                    nextCursorInt,
                                    nextPage)*/
                            getCallbackNextPageDTO(cursor, nextCursorInt, nextPage, serializableInlineType)))
                    .build());
        }
        return pagination;
    }

    protected List<InlineKeyboardButton> prepareAuctionInformation(
            Long chatId,
            Integer messageId,
            Integer cursorInt) {
        return Collections.singletonList(InlineKeyboardButton.builder()
                .text("Информация об аукционе")
                .callbackData(StringUtil.serialize(new AlertAuctionInformationDTO(chatId, messageId, cursorInt)))
                .build());
    }

    protected InlineKeyboardMarkup getInlineKeyboardMarkup(List<InlineKeyboardButton> pagination,
                                                           List<InlineKeyboardButton> auctionInformation) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(pagination)
                .keyboardRow(auctionInformation)
                .build();
    }

    protected CallbackNextPageDTO getCallbackNextPageDTO(int granCursor, int previousCursor, int previousPage, SerializableInlineType serializableInlineType) {
        if (serializableInlineType.equals(SerializableInlineType.NEXT_CADASTRAL_PAGE)) {
            return new CallbackNextPageDTO(
                    granCursor,
                    previousCursor,
                    previousPage);
        } else if (serializableInlineType.equals(SerializableInlineType.NEXT_GEOLOCATION_PAGE)) {
            return new CallbackNextPageDTO(
                    SerializableInlineType.NEXT_GEOLOCATION_PAGE,
                    granCursor,
                    previousCursor,
                    previousPage);
        } else if (serializableInlineType.equals(SerializableInlineType.NEXT_ALL_OBJECTS_PAGE)) {
            return new CallbackNextPageDTO(
                    SerializableInlineType.NEXT_ALL_OBJECTS_PAGE,
                    granCursor,
                    previousCursor,
                    previousPage);
        } else {
            return null;
        }
    }

    protected List<InlineKeyboardButton> prepareLeftButtonForPagination(int page, int previousCursor, MessageIdDTO messageIdDTO,
                                                                        SerializableInlineType serializableInlineType) {
        List<InlineKeyboardButton> pagination = new ArrayList<>();
        int previousPage = page - 1;
        if (page == 1) {
            pagination.add(
                    InlineKeyboardButton.builder()
                            .text(Constants.BUTTON_LEFT)
                            .callbackData(
                                    StringUtil.serialize(new AlertDTO(Constants.MESSAGE_FIRST_PAGE, false))
                            )
                            .build()
            );
        } else if (page > 2) {
            int granCursor = temporaryDataService.getCursor(messageIdDTO, previousCursor);
            pagination.add(
                    InlineKeyboardButton.builder()
                            .text(Constants.BUTTON_LEFT)
                            .callbackData(
                                    StringUtil.serialize(
                                            getCallbackNextPageDTO(
                                                    granCursor,
                                                    previousCursor,
                                                    previousPage,
                                                    serializableInlineType))
                            )
                            .build()
            );
        } else if (page == 2) {
            pagination.add(
                    InlineKeyboardButton.builder()
                            .text(Constants.BUTTON_LEFT)
                            .callbackData(
                                    StringUtil.serialize(
                                            getCallbackNextPageDTO(
                                                    Integer.parseInt(Constants.CURSOR_INIT),
                                                    previousCursor,
                                                    previousPage,
                                                    serializableInlineType
                                            ))
                            )
                            .build()
            );
        }
        return pagination;
    }

    protected void handleResponse(Optional<RealEstateResponseDTO<RealEstateDTO>> response,
                                  Message message,
                                  MessageIdDTO messageIdDTO,
                                  PaginationControlValuesDTO paginationControlValuesDTO,
                                  int previousCursor
    ) throws TelegramApiException {
        temporaryDataService.setCursor(messageIdDTO, Integer.parseInt(paginationControlValuesDTO.getCursor()), previousCursor);
        List<InlineKeyboardButton> pagination =
                new ArrayList<>(prepareLeftButtonForPagination(paginationControlValuesDTO.getPage(), previousCursor, messageIdDTO, getSerializableType()));
//        PaginationControlValuesDTO paginationControlValuesDTO = new PaginationControlValuesDTO(String.valueOf(cursor), quantity, page);
        if (response.isPresent()) {
            editMessage(message, response, pagination, messageIdDTO, paginationControlValuesDTO, getSerializableType());
        }
    }
}
