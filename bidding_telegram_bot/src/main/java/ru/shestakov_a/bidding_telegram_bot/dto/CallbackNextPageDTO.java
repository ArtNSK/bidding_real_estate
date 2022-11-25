package ru.shestakov_a.bidding_telegram_bot.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallbackNextPageDTO extends SerializableInlineObject {
    @JsonProperty
    private int previousCursor;

    @JsonProperty
    private int nextCursor;

    @JsonProperty
    private int page;


    public CallbackNextPageDTO() {
        super(SerializableInlineType.NEXT_CADASTRAL_PAGE);
    }


    public CallbackNextPageDTO(int previousCursor, int nextCursor, int page) {
        this();
        this.previousCursor = previousCursor;
        this.nextCursor = nextCursor;
        this.page = page;
    }

    public CallbackNextPageDTO(SerializableInlineType type, int previousCursor, int nextCursor, int page) {
        super(type);
        this.previousCursor = previousCursor;
        this.nextCursor = nextCursor;
        this.page = page;
    }
}