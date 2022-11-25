package ru.shestakov_a.bidding_app.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.shestakov_a.bidding_app.exception.EntityNotFoundException;
import ru.shestakov_a.bidding_app.model.error.DefaultErrorResponse;


import javax.validation.ValidationException;

/**
 * Класс для обработки исключений на уровне контроллера
 * @author Shestakov Artem
 * */
@ControllerAdvice
@Slf4j
public class ExceptionAdvisor {
    private static final String MESSAGE_INPUT_PARAMETER_ERROR = "Ошибка входных параметров";
    private static final String MESSAGE_METHOD_NOT_ALLOWED = "Метод не поддерживается";
    private static final String MESSAGE_UNEXPECTED_ERROR = "Произошла непредвиденная ошибка";


    /**
     * Метод обрабатывает исключение, если сущности нет в базе данных
     * @param e - исключение
     * @return Сущность не найдена (status code 404)
     * */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DefaultErrorResponse> handleMissingEntity(Exception e) {
        log.info("Сущность не найдена: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponse(e.getMessage()));
    }

    /**
     * Метод обрабатывает исключения для ошибочных входных параметров
     * @param e - исключение
     * @return Ошибка входных параметров (status code 400)
     * */
    @ExceptionHandler({ValidationException.class,
            IllegalArgumentException.class,
            MissingRequestValueException.class,
            HttpMediaTypeNotSupportedException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<DefaultErrorResponse> handleBadRequest(Exception e)
    {
        log.warn("Ошибка входных параметров: {}", e.getMessage());
        String message = MESSAGE_INPUT_PARAMETER_ERROR;
        if (StringUtils.hasText(e.getMessage())) {
            message = e.getMessage();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DefaultErrorResponse(message));
    }

   /**
     * Метод обрабатывает исключения при ошибочном запросе
     * @param e - исключение
     * @return Метод не поддерживается (status code 405)
     * */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DefaultErrorResponse> handleWrongMethodRequest(Exception e) {
        log.warn("Метод не поддерживается : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new DefaultErrorResponse(MESSAGE_METHOD_NOT_ALLOWED));
    }

    /**
     * Метод обрабатывает непредвиденные исключения
     * @param e - исключение
     * @return Непредвиденная ошибка (status code 500)
     **/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultErrorResponse> handleUnexpectedException(Exception e) {
        log.error("Непредвиденная ошибка {}: {}", e.getClass().getName(),  e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DefaultErrorResponse(MESSAGE_UNEXPECTED_ERROR));
    }
}