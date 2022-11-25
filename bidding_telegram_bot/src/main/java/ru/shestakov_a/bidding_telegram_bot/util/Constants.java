package ru.shestakov_a.bidding_telegram_bot.util;

public final class Constants {
    private Constants() {
    }

    public static final int DISPLAY_LIMIT = 1;
    public static final int ONLY_RESPONSE = 0;
    public static final int PAGE_INIT = 1;
    public static final int QUANTITY_LIMIT = 100;
    public static final String CURSOR_INIT = "0";
    public static final String BUTTON_LEFT = "◀";
    public static final String BUTTON_RIGHT = "▶";
    public static final String BUTTON_SEND_GEOLOCATION = "Отправить геопозицию";
    public static final String EMPTY_MESSAGE = "";
    public static final String WELCOME_MESSAGE = "Welcome to the Bot!\nPlease choose some operation";
    public static final String MESSAGE_FIRST_PAGE = "Начальная страница";
    public static final String MESSAGE_LAST_PAGE = "Последняя страница";
    public static final String MESSAGE_SEND_GEOLOCATION = "Отправьте геопозицию";
    public static final String MESSAGE_SEND_CADASTRAL_NUMBER = "Введите кадастровый номер";
    public static final String MESSAGE_NUMBER_NOT_FOUND = "Кадастровый номер не найден";
    public static final String MESSAGE_UPDATE_BOT = "Обновите бота";
    public static final String MESSAGE_LOCATION_NOT_FOUND = "Нет данных для локации";
    public static final String PARSE_HTML = "html";
}