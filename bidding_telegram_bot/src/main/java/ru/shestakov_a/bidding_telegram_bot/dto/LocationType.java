package ru.shestakov_a.bidding_telegram_bot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LocationType {
    CITY(0),
    REGION(1);
    private final int index;
}