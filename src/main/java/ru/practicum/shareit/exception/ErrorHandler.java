package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
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

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ErrorResponse handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return new ErrorResponse("EmailAlreadyExists error", e.getMessage());
    }
}