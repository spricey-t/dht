package com.virohtus.dht.rest.serialize;

public class ErrorResponse {

    private String message;
    private Exception e;

    public ErrorResponse(String message, Exception e) {
        this.message = message;
        this.e = e;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
}
