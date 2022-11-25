package ru.shestakov_a.bidding_app.model.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DefaultErrorResponse {
    @JsonProperty("message")
    @NotBlank
    private String message;

    @JsonProperty("timestamp")
    @NotBlank
    private Timestamp timestamp;

    public DefaultErrorResponse(String message) {
        this.message = message;
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
    }
}