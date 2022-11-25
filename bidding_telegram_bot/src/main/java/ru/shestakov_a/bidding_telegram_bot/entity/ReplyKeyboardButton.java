package ru.shestakov_a.bidding_telegram_bot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum содержит значения для кнопок меню бота
 * */
@Getter
public enum ReplyKeyboardButton {
    CADASTRAL("Узнать об объекте по кадастровому номеру \uD83C\uDFE1"),
    ABOUT("О боте \uD83D\uDCD6"),
    /*CITY("Недвижимость в городе"),*/
    ALL_CADASTRAL("Доступная недвижимость"),
    REGION("Недвижимость в регионе");

    private final String label;
    private static final String CALLBACK_DATA_FIELD = "callback_data";

    @JsonProperty("callback_data")
    private String callbackData;


    ReplyKeyboardButton(String label) {
        this.label = label;
    }

    ReplyKeyboardButton(String label, String callbackData) {
        this.label = label;
        this.callbackData = callbackData;
    }

    public static Optional<ReplyKeyboardButton> parse(String name) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(name) || b.label.equalsIgnoreCase(name))
                .findFirst();
    }
}
