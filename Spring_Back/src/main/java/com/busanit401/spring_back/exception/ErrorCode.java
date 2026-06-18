package com.busanit401.spring_back.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "유저를 찾을 수 없습니다."),
    DELETED_USER(HttpStatus.BAD_REQUEST, "U002", "탈퇴한 유저입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 사용 중인 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "U004", "이미 사용 중인 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "U005", "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U006", "현재 비밀번호가 올바르지 않습니다."),
    PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "U007", "새 비밀번호가 일치하지 않습니다."),

    // Notification
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "알림을 찾을 수 없습니다."),

    // Subscription
    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "구독 정보를 찾을 수 없습니다."),
    DUPLICATE_SUBSCRIPTION(HttpStatus.CONFLICT, "S002", "이미 구독 중인 숙소입니다."),
    ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "S003", "이미 처리된 초대입니다."),
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "S004", "이미 초대된 멤버입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "S005", "다음 유저를 찾을 수 없습니다: "),

    // Payment
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "결제 정보를 찾을 수 없습니다."),
    ALREADY_REFUNDED(HttpStatus.BAD_REQUEST, "P002", "이미 환불된 내역입니다."),
    REFUND_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "P003", "환불 금액이 결제 금액을 초과합니다."),

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 오류가 발생했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}