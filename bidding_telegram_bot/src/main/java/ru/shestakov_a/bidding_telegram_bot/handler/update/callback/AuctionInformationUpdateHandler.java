package ru.shestakov_a.bidding_telegram_bot.handler.update.callback;

import ru.shestakov_a.bidding_telegram_bot.bots.BiddingBot;
import ru.shestakov_a.bidding_telegram_bot.dto.AlertAuctionInformationDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.MessageIdDTO;
import ru.shestakov_a.bidding_telegram_bot.dto.SerializableInlineType;
import ru.shestakov_a.bidding_telegram_bot.service.TemporaryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import real_estate.dto.bot.AuctionInformationDTO;
import real_estate.response.ResponseUtil;

/**
 * Класс для отправки Alert сообщений с информацией по аукциону при соответствующем Callback запросе
 * */
@Component
public class AuctionInformationUpdateHandler extends CallbackUpdateHandler<AlertAuctionInformationDTO> {
    @Autowired
    @Lazy
    private BiddingBot bot;

    @Autowired
    private TemporaryDataService temporaryDataService;

    @Override
    protected Class<AlertAuctionInformationDTO> getDtoType() {
        return AlertAuctionInformationDTO.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.ALERT_AUCTION_INFORMATION;
    }

    @Override
    protected void handleCallback(Update update, AlertAuctionInformationDTO dto) throws TelegramApiException {
        MessageIdDTO messageIdDTO = new MessageIdDTO(dto.getChatId(), dto.getMessageId());
        AuctionInformationDTO auctionInformationDTO =
                temporaryDataService.getAuctionInformation(messageIdDTO, dto.getCursor());
        String responseString = ResponseUtil.getAuctionInformationText(auctionInformationDTO);
        bot.execute(
                AnswerCallbackQuery.builder()
                        .cacheTime(10)
                        .text(responseString)
                        .showAlert(true)
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .build()
        );
    }
}