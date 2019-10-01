package com.juyoung.restapiwithspring.accounts;

import com.juyoung.restapiwithspring.error.ErrorCode;
import com.juyoung.restapiwithspring.error.PlatformException;

public class NotMatchAccountException extends PlatformException {

    private static final ErrorCode ERROR_CODE = ErrorCode.FORBIDDEN;

    private static final String ERROR_MESSAGE = "계정이 일치하지 않습니다.";

    public NotMatchAccountException() {
        super(ERROR_CODE, ERROR_MESSAGE);
    }
}
