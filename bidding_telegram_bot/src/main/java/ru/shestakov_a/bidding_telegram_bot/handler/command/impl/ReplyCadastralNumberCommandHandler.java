package ru.shestakov_a.bidding_telegram_bot.handler.command.impl;

import real_estate.dto.bot.AuctionInformationDTO;
import real_estate.dto.controller.QuantityRealEstateCadastralNumberDTO;
import real_estate.dto.controller.RealEstateDTO;
import real_estate.dto.controller.RealEstateResponseDTO;
import real_estate.response.ResponseUtil;
import ru.shestakov_a.bidding_telegram_bot.bots.BiddingBot;
import ru.shestakov_a.bidding_telegram_bot.client.DbRestClient;
import ru.shestakov_a.bidding_telegram_bot.dto.*;
import ru.shestakov_a.bidding_telegram_bot.entity.Command;
import ru.shestakov_a.bidding_telegram_bot.exception.EntityNotFoundException;
import ru.shestakov_a.bidding_telegram_bot.handler.command.CommandHandler;
import ru.shestakov_a.bidding_telegram_bot.service.TemporaryDataService;
import ru.shestakov_a.bidding_telegram_bot.util.Constants;
import ru.shestakov_a.bidding_telegram_bot.util.MessageUtil;
import util.StringUtil;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интрерфейса обработчика команды {@link Command#REPLY_CADASTRAL_NUMBER }
 * */
@Component
@Slf4j
public class ReplyCadastralNumberCommandHandler implements CommandHandler {

    @Autowired
    @Lazy
    private BiddingBot bot;

    @Autowired
    private DbRestClient dbRestClient;

    @Autowired
    private TemporaryDataService temporaryDataService;

    /**
     * Метод обрабатывает входящюю команду. При этом происходит парсинг кадастрового номера,
     * обращение к серверу приложения торгов по недвижимости, получение ответа и отправление
     * сообщения с ответом клиенту.
     * */
    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        try {
            Optional<String> cadastralNumber = MessageUtil.parseCadastralNumber(text);
            if (cadastralNumber.isPresent()) {
                String trimCadastralNumber = StringUtil.trim(cadastralNumber.get());
                Optional<RealEstateResponseDTO<RealEstateDTO>> response = getCadastralNumberFromDb(trimCadastralNumber);
                if (response.isPresent()) {
                    sendMessageWithResponse(message, response, trimCadastralNumber);
                }
            } else {
                sendMessageCadastralNumberRequest(message);
            }

        } catch (FeignException e) {
            log.info("Кадастровый номер не найден: {}", e.getMessage());
            sendMessageWithoutCadastralNumber(message);
        }
    }

    private void sendMessageWithResponse(Message message,
                                         Optional<RealEstateResponseDTO<RealEstateDTO>> response,
                                         String trimCadastralNumber) throws TelegramApiException {
        RealEstateResponseDTO<RealEstateDTO> responseDto = response.get();
        RealEstateDTO responseRealEstateDTO = responseDto.getItems().get(Constants.ONLY_RESPONSE);
        PaginationControlValuesDTO paginationControlValuesDTO = getPaginationControlValues(responseDto,
                trimCadastralNumber, message);

        MessageIdDTO messageIdDTO = new MessageIdDTO(message.getChatId(), message.getMessageId());
        List<InlineKeyboardButton> auctionInformation =  prepareAuctionInformation(message.getChatId(),
                message.getMessageId(), Integer.parseInt(paginationControlValuesDTO.getCursor()));
        AuctionInformationDTO auctionInformationDTO = getAuctionInformation(responseRealEstateDTO);
        temporaryDataService.setAuctionInformation(messageIdDTO, Integer.parseInt(paginationControlValuesDTO.getCursor()),
                auctionInformationDTO);

        setTemporaryValuesForResponse(message, trimCadastralNumber, paginationControlValuesDTO.getQuantity());

        String responseString = ResponseUtil.getHtml(
                responseDto.getItems().get(Constants.ONLY_RESPONSE),
                Constants.PAGE_INIT, paginationControlValuesDTO.getQuantity());
        InlineKeyboardMarkup inlineKeyboardMarkup = prepareInlineKeyboard(paginationControlValuesDTO, auctionInformation);

        MessageDataDTO messageDataDTO = MessageDataDTO.builder()
                .text(responseString)
                .chatId(message.getChatId())
                .parseMode(Constants.PARSE_HTML)
                .replyMarkup(inlineKeyboardMarkup)
                .build();

        MessageUtil.sendMessage(bot, messageDataDTO);
    }

    private void sendMessageCadastralNumberRequest(Message message) throws TelegramApiException {
        MessageDataDTO messageDataDTO = MessageDataDTO.builder().chatId(message.getChatId())
                .text(Constants.MESSAGE_SEND_CADASTRAL_NUMBER).build();

        MessageUtil.sendMessage(bot, messageDataDTO);
    }

    private void sendMessageWithoutCadastralNumber(Message message) throws TelegramApiException {
        MessageDataDTO messageDataDTO = MessageDataDTO.builder().chatId(message.getChatId())
                .text(Constants.MESSAGE_NUMBER_NOT_FOUND).build();

        MessageUtil.sendMessage(bot, messageDataDTO);
    }

    @Override
    public Command getCommand() {
        return Command.REPLY_CADASTRAL_NUMBER;
    }

    private Optional<RealEstateResponseDTO<RealEstateDTO>> getCadastralNumberFromDb(String cadastralNumber) {
        return dbRestClient.getRealEstateList(Constants.DISPLAY_LIMIT, Constants.CURSOR_INIT, cadastralNumber);
    }

    private Optional<QuantityRealEstateCadastralNumberDTO> getQuantityFromDb(String cadastralNumber) {
        return dbRestClient.getRealEstateQuantityByCadastralNumber(cadastralNumber);
    }


    private PaginationControlValuesDTO getPaginationControlValues(RealEstateResponseDTO<RealEstateDTO> responseDto,
                                                                  String cadastralNumber,
                                                                  Message message) {
        String cursor = responseDto.getCursor();
        int quantity = getQuantityFromDb(cadastralNumber).orElseThrow(() ->
        {
            EntityNotFoundException entityNotFoundException = new EntityNotFoundException();
            entityNotFoundException.setChatId(message.getChatId());
            return entityNotFoundException;

        }).getQuantity();
        return new PaginationControlValuesDTO(cursor, quantity);
    }

    private InlineKeyboardMarkup prepareInlineKeyboard(PaginationControlValuesDTO paginationControlValuesDTO,
                                                       List<InlineKeyboardButton> auctionInformation) {
        List<InlineKeyboardButton> pagination = new ArrayList<>();
        int cursorInt = Integer.parseInt(paginationControlValuesDTO.getCursor());
        int currentCursorInt = Integer.parseInt(Constants.CURSOR_INIT);
        int page = Constants.PAGE_INIT;
        int nextPage = page + 1;
        if (isPaginationNecessary(page, paginationControlValuesDTO.getQuantity())) {
            pagination.add(
                    InlineKeyboardButton.builder()
                            .text(Constants.BUTTON_LEFT)
                            .callbackData(StringUtil.serialize(new AlertDTO(Constants.MESSAGE_FIRST_PAGE, false)))
                            .build()
            );
            pagination.add(
                    InlineKeyboardButton.builder()
                            .text(Constants.BUTTON_RIGHT)
                            .callbackData(StringUtil.serialize(new CallbackNextPageDTO(currentCursorInt, cursorInt, nextPage)))
                            .build());
        }


        return InlineKeyboardMarkup.builder()
                .keyboardRow(pagination)
                .keyboardRow(auctionInformation)
                .build();
    }

    private boolean isPaginationNecessary(int page, int quantity) {
        return page < quantity;
    }

    private void setTemporaryValuesForResponse(Message message, String cadastralNumber, int quantity) {
        int nextMessageId = message.getMessageId() + 1;
        MessageIdDTO messageIdDto = new MessageIdDTO(message.getChatId(), nextMessageId);

        temporaryDataService.setCadastralNumber(messageIdDto, cadastralNumber);
        temporaryDataService.setQuantity(messageIdDto, quantity);
    }

    private AuctionInformationDTO getAuctionInformation(RealEstateDTO responseRealEstateDTO) {
        return AuctionInformationDTO.builder()
                .biddingStartTime(responseRealEstateDTO.getBiddingStartTime())
                .biddingEndTime(responseRealEstateDTO.getBiddingEndTime())
                .auctionStartDate(responseRealEstateDTO.getAuctionStartDate())
                .build();
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
}