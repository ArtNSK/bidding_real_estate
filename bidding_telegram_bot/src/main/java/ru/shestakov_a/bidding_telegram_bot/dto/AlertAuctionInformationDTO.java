package ru.shestakov_a.bidding_telegram_bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertAuctionInformationDTO extends SerializableInlineObject {
    @JsonProperty("c")
    private Long chatId;

    @JsonProperty("m")
    private Integer messageId;

    @JsonProperty("cur")
    private Integer cursor;

    public AlertAuctionInformationDTO(){
        super(SerializableInlineType.ALERT_AUCTION_INFORMATION);
    }

    public AlertAuctionInformationDTO(Long chatId, Integer messageId, Integer cursor) {
        this();
        this.chatId = chatId;
        this.messageId = messageId;
        this.cursor = cursor;
    }
}
