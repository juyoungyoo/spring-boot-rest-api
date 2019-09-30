package com.juyoung.restapiwithspring.events;

import com.juyoung.restapiwithspring.error.ErrorCode;
import com.juyoung.restapiwithspring.error.PlatformException;

class NotFoundException extends PlatformException {

    private static final ErrorCode ERROR_CODE = ErrorCode.NOT_FOUND;
    private static final String ERROR_MESSAGE = "이벤트가 없습니다";

    NotFoundException() {
        super(ERROR_CODE, ERROR_MESSAGE);
    }
}
