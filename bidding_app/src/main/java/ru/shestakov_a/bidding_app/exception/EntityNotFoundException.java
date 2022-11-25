package ru.shestakov_a.bidding_app.exception;

public class EntityNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Entity not found";

    public EntityNotFoundException(String message) {
        super(message);
    }
    public EntityNotFoundException()

    {
        super(DEFAULT_MESSAGE);
    }
}