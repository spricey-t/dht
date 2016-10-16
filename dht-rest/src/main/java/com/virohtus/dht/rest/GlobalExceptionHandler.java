package com.virohtus.dht.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Exception handleException(Exception e) {
        return e;
    }

    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public Exception handleTimeout(TimeoutException e) {
        return e;
    }

    @ExceptionHandler(InterruptedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Exception handleInterrupted(InterruptedException e) {
        return e;
    }
}
