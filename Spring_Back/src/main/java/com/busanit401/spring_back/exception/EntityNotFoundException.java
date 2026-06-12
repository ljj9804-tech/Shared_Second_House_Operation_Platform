package com.busanit401.spring_back.exception;

public class EntityNotFoundException extends CustomException {
    public EntityNotFoundException(ErrorCode memberNotFound) {
        super(memberNotFound);
    }
}
