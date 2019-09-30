package com.juyoung.restapiwithspring.error;


import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.Nullable;

import java.util.Objects;

@Getter
class FieldError {

    private final String field;
    @Nullable
    private final String value;
    private final String reason;

    @Builder
    private FieldError(final String field,
                       final String value,
                       final String reason) {
        this.field = field;
        this.value = value;
        this.reason = reason;
    }

    static FieldError of(org.springframework.validation.FieldError fieldError) {
        return FieldError.builder()
                .field(fieldError.getField())
                .value(parseValue(fieldError))
                .reason(fieldError.getDefaultMessage())
                .build();
    }

    private static String parseValue(org.springframework.validation.FieldError fieldError) {
        if (Objects.isNull(fieldError.getRejectedValue())) {
            return Strings.EMPTY;
        }
        return fieldError.getRejectedValue().toString();
    }
}