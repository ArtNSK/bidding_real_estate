package ru.shestakov_a.bidding_telegram_bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertDTO extends SerializableInlineObject {
  @JsonProperty("m")
  private String message;

  @JsonProperty("f")
  private boolean fullMode;

  public AlertDTO() {
    super(SerializableInlineType.ALERT);
  }

  public AlertDTO(String message, boolean fullMode) {
    this();
    this.message = message;
    this.fullMode = fullMode;
  }
}
