package com.busanit401.spring_back.exception;

public class DuplicateException extends CustomException {
    public DuplicateException(ErrorCode message) {
        super(message);
    }
}