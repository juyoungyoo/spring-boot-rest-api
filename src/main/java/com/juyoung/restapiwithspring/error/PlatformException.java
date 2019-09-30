package com.juyoung.restapiwithspring.error;

import org.springframework.http.ResponseEntity;

public abstract class PlatformException extends RuntimeException {

    private final ErrorCode errorCode;

    public PlatformException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    ResponseEntity<ErrorResponse> toErrorResponse() {
        return ResponseEntity.status(errorCode.getStatus())
                .body(initErrorResponse());
    }

    private ErrorResponse initErrorResponse() {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(this.getMessage())
                .build();
    }
}
