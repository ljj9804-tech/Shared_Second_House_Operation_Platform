package com.busanit401.spring_back.exception;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResp {

    private final String code;       // 에러 코드 (예: U001)
    private final String message;    // 에러 메시지
    private final LocalDateTime timestamp;

    public static ErrorResp of(ErrorCode errorCode) {
        return ErrorResp.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResp of(ErrorCode errorCode, String additionalMessage) {
        return ErrorResp.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage() + additionalMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
}