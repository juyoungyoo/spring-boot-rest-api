package com.juyoung.restapiwithspring.accounts.password;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.juyoung.restapiwithspring.accounts.password.WrongPasswordException.ERROR_MESSAGE_OF_CONDITION;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    static final String REGEX_OF_PASSWORD = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{4,}$";
    static final int MIN_LENGTH = 4;

    @Column(name = "password")
    private String value;

    private Password(final String value) {
        this.value = value;
    }

    public static Password of(final String value) {
        validateLength(value);
        validateCondition(value);
        return new Password(value);
    }

    private static void validateLength(String value) {
        if (MIN_LENGTH > value.length()) {
            throw new WrongPasswordException();
        }
    }

    private static void validateCondition(String value) {
        if (!Pattern.matches(REGEX_OF_PASSWORD, value)) {
            throw new WrongPasswordException(String.format(ERROR_MESSAGE_OF_CONDITION, value));
        }
    }

    public void encode(PasswordEncoder passwordEncoder) {
        this.value = passwordEncoder.encode(value);
    }

    public boolean isMatched(final Password target, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(target.value, this.value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Password{" +
                "value='" + value + '\'' +
                '}';
    }
}