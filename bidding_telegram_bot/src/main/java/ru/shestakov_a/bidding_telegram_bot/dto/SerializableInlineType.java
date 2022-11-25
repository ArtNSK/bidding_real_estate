package ru.shestakov_a.bidding_telegram_bot.dto;

public enum SerializableInlineType {
  ALERT(0),
  NEXT_CADASTRAL_PAGE(1),
  NEXT_GEOLOCATION_PAGE(2),
  NEXT_ALL_OBJECTS_PAGE(3),
  FIRST_NEXT_PAGE_LOCATION(4),
  ALERT_AUCTION_INFORMATION(5);

  private final int index;

  SerializableInlineType(int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
  }
}
