package com.juyoung.restapiwithspring.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    UNAUTHORIZED("ERR001", HttpStatus.UNAUTHORIZED),
    NOT_FOUND("ERR002", HttpStatus.NOT_FOUND),
    INPUT_VALUE_INVALID("ERR003", HttpStatus.BAD_REQUEST),
    FORBIDDEN("ERR004", HttpStatus.FORBIDDEN);

    private final String code;
    private final HttpStatus status;

    ErrorCode(final String code, final HttpStatus status) {
        this.code = code;
        this.status = status;
    }
}
