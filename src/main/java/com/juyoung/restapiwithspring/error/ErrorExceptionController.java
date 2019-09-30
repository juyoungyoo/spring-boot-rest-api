package com.juyoung.restapiwithspring.error;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorExceptionController {

    private static final String ERROR_MESSAGE_OF_DEFAULT = "입력값이 올바르지 않습니다.";

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        final List<FieldError> fieldErrors = getFieldErrors(exception.getBindingResult());

        return ErrorResponse.builder()
                .code(ErrorCode.INPUT_VALUE_INVALID.getCode())
                .message(ERROR_MESSAGE_OF_DEFAULT)
                .errors(fieldErrors)
                .build();
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handlePlatformExeption(PlatformException exception) {
        return exception.toErrorResponse();
    }

    private List<FieldError> getFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::of)
                .collect(Collectors.toList());
    }
}