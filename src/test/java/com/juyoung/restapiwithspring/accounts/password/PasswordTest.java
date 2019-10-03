package com.juyoung.restapiwithspring.accounts.password;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.juyoung.restapiwithspring.accounts.password.Password.MIN_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PasswordTest {

    private static final Logger log = LoggerFactory.getLogger(PasswordTest.class);

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @DisplayName("비밀번호 생성 성공")
    @ParameterizedTest
    @ValueSource(strings = {"test1", "test2", "1test"})
    void create_success(String password) {

        Password result = Password.of(password);
        log.debug("Password value : {}", result);

        result.encode(passwordEncoder);
        log.debug("Password hashing value : {}", result);

        assertThat(result).isNotNull();
    }

    @DisplayName("비밀번호 일치하는 지 확인한다")
    @ParameterizedTest
    @CsvSource({"'test1', 'test1', true",
            "'test1', 'test2', false"})
    void isMatched_whenPasswordEquals(String origin, String target, boolean expectedResult) {
        Password originPassword = Password.of(origin);
        originPassword.encode(passwordEncoder);

        Password confirmPassword = Password.of(target);

        boolean result = originPassword.isMatched(confirmPassword, passwordEncoder);

        assertThat(result).isEqualTo(expectedResult);
    }

    @DisplayName("비밀번호가 " + MIN_LENGTH + "자미만일 시 실패")
    @Test
    void create_passwordLessThanMinLength_thenException() {
        String password = "i23";

        assertThatExceptionOfType(WrongPasswordException.class)
                .isThrownBy(() -> Password.of(password));
    }

    @DisplayName("영어와 숫자를 조합이 아닐 시 Exception")
    @ParameterizedTest
    @CsvSource({"1234", "password"})
    void create_passwordNoMix_thenException(String wrongPassword) {
        assertThatExceptionOfType(WrongPasswordException.class)
                .isThrownBy(() -> Password.of(wrongPassword));
    }
}