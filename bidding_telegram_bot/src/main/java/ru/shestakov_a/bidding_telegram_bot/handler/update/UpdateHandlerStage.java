package ru.shestakov_a.bidding_telegram_bot.handler.update;

/**
 * Enum содержащий возможные типы {@link org.telegram.telegrambots.meta.api.objects.Update} для данного бота
 * */
public enum UpdateHandlerStage {
    CALLBACK,
    LOCATION,
    COMMAND,
    COMMAND_CADASTRAL_NUMBER,
    REPLY_BUTTON;

    public int getOrder() {
        return ordinal();
    }
}