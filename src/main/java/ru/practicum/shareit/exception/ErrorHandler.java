package ru.practicum.shareit.exception;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice()
public class ErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IdNotFoundException.class)
    public ErrorResponse handleIdNotFoundException(IdNotFoundException e) {
        return new ErrorResponse("IdNotFound error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConversionFailedException.class)
    public ErrorResponse handleUnknownStateException(ConversionFailedException e) {
        return new ErrorResponse("Unknown state: " + e.getValue().toString(), "неправильный статус");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnavailableException.class)
    public ErrorResponse handleUnavailableException(UnavailableException e) {
        return new ErrorResponse("Unavailable error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        String defaultMessage;
        if (e.getFieldError() == null) {
            defaultMessage = e.getGlobalError().getDefaultMessage();
        } else defaultMessage = e.getFieldError().getDefaultMessage();
        return new ErrorResponse("Validation error", defaultMessage);
    }
}