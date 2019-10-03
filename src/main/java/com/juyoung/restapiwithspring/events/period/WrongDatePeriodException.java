package com.juyoung.restapiwithspring.events.period;

import com.juyoung.restapiwithspring.error.ErrorCode;
import com.juyoung.restapiwithspring.error.PlatformException;

import java.time.LocalDateTime;

public class WrongDatePeriodException extends PlatformException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INPUT_VALUE_INVALID;

    private static final String ERROR_MESSAGE = "입력한 기간이 잘못되었습니다. (시작 날짜: %s, 마감 날짜: %s)";

    WrongDatePeriodException(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(ERROR_CODE, String.format(ERROR_MESSAGE, startDateTime, endDateTime));
    }
}
