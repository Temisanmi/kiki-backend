package com.example.kiki.exception;

public class ExpiredOrInvalidTokenException extends RuntimeException {
    public ExpiredOrInvalidTokenException(String message) {
        super(message);
    }
}
