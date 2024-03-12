package com.darwgom.tradibankapi.domain.exceptions;

public class IllegalParamException extends RuntimeException {
    public IllegalParamException(String message) {
        super(message);
    }
}
