package com.virohtus.dht.rest;

import com.virohtus.dht.rest.serialize.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        return new ErrorResponse("error occurred", e);
    }

    @ExceptionHandler(InterruptedException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ErrorResponse handleTimeout(InterruptedException e) {
        return new ErrorResponse("request timed out", e);
    }
}
