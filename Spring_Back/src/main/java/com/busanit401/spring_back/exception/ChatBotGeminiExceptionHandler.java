package com.busanit401.spring_back.exception;

import com.busanit401.spring_back.controller.ChatBotController;
import com.busanit401.spring_back.dto.ChatBotAiErrorResponseDTO;
import com.busanit401.spring_back.exception.ChatBotGeminiApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

/**
 * Gemini API 컨트롤러에서 발생하는 예외를 JSON 형태로 반환해
 * 프론트엔드가 일관된 에러 메시지를 표시할 수 있게 합니다.
 */
@RestControllerAdvice(assignableTypes = ChatBotController.class)
@Log4j2
public class ChatBotGeminiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ChatBotAiErrorResponseDTO> handleBadRequest(
            IllegalArgumentException e,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", e.getMessage(), request);
    }

    @ExceptionHandler(ChatBotGeminiApiException.class)
    public ResponseEntity<ChatBotAiErrorResponseDTO> handleGeminiApiException(
            ChatBotGeminiApiException e,
            HttpServletRequest request) {
        log.warn("Gemini upstream error: status={}, code={}, message={}",
                e.getStatus(), e.getCode(), e.getMessage());
        return buildErrorResponse(e.getStatus(), e.getCode(), e.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ChatBotAiErrorResponseDTO> handleIllegalState(
            IllegalStateException e,
            HttpServletRequest request) {
        log.error("Gemini internal error", e);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "AI_INTERNAL_ERROR",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ChatBotAiErrorResponseDTO> handleUnexpected(
            Exception e,
            HttpServletRequest request) {
        log.error("Unexpected Gemini controller error", e);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "UNEXPECTED_ERROR",
                "알 수 없는 오류가 발생했습니다.",
                request
        );
    }

    private ResponseEntity<ChatBotAiErrorResponseDTO> buildErrorResponse(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request) {
        ChatBotAiErrorResponseDTO body = ChatBotAiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
