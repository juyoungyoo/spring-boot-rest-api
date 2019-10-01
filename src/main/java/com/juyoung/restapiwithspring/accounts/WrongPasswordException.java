package com.juyoung.restapiwithspring.accounts;

import com.juyoung.restapiwithspring.error.ErrorCode;
import com.juyoung.restapiwithspring.error.PlatformException;

import static com.juyoung.restapiwithspring.accounts.Password.MIN_LENGTH;

class WrongPasswordException extends PlatformException {

    private static ErrorCode ERROR_CODE = ErrorCode.UNAUTHORIZED;

    private static final String ERROR_MESSAGE_OF_LENGTH = String.format("패스워드는 %d글자 이상이어야 합니다.", MIN_LENGTH);
    static final String ERROR_MESSAGE_OF_CONDITION = "패스워드는 영어, 숫자 혼합되어야 합니다. (입력값: %s)";

    WrongPasswordException() {
        super(ERROR_CODE, ERROR_MESSAGE_OF_LENGTH);
    }

    WrongPasswordException(String message) {
        super(ERROR_CODE, message);
    }
}
