package ru.shestakov_a.bidding_telegram_bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class GeolocationDTO {

    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lon")
    private Double lon;
}