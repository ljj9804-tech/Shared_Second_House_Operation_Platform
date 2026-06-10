package com.busanit401.spring_back.exception;

public class InvalidStateException extends CustomException {
    public InvalidStateException(ErrorCode message) {
        super(message);
    }
}