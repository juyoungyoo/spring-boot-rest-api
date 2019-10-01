package com.juyoung.restapiwithspring.accounts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static com.juyoung.restapiwithspring.accounts.Password.REGEX_OF_PASSWORD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RegexTest {

    @DisplayName("영문, 숫자 혼합하며 4글자 이상일 시 성공")
    @ParameterizedTest
    @ValueSource(strings = {"asd3", "3asd", "a3sd"})
    void regexTest_success(String password) {

        Boolean result = Pattern.matches(REGEX_OF_PASSWORD, password);

        assertThat(result).isTrue();
    }

    @DisplayName("영문, 숫자 혼합 아닐 시 실패")
    @ParameterizedTest
    @ValueSource(strings = {"asdf", "1234"})
    void regexTest_fail(String wrongPassword) {
        Boolean result = Pattern.matches(REGEX_OF_PASSWORD, wrongPassword);

        assertThat(result).isFalse();
    }
}