package ru.shestakov_a.bidding_telegram_bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationIndexDTO extends SerializableInlineObject {
    @JsonProperty("t")
    private int typeIndex;

    public LocationIndexDTO() {
        super(SerializableInlineType.FIRST_NEXT_PAGE_LOCATION);
    }

    public LocationIndexDTO(LocationType locationType) {
        this();
        this.typeIndex = locationType.getIndex();

    }
}