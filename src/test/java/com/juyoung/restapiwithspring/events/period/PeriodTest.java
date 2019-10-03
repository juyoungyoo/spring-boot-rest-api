package com.juyoung.restapiwithspring.events.period;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class PeriodTest {

    @DisplayName("시작날짜와 마감날짜를 가지는 기간을 생성하는데 성공")
    @Test
    void initialize() {
        LocalDateTime startDate = LocalDateTime.of(2019, 10, 1, 12, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2019, 10, 2, 12, 0, 0);

        Period result = Period.of(startDate, endDate);

        assertThat(result).isNotNull();
    }

    @DisplayName("시작날짜가 마감날짜이후 일 시 exception")
    @Test
    void of_whenStartDateOverThanEndDate_thenException() {
        LocalDateTime startDate = LocalDateTime.of(2019, 10, 2, 12, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2019, 10, 1, 12, 0, 0);

        assertThatExceptionOfType(WrongDatePeriodException.class)
                .isThrownBy(() -> Period.of(startDate, endDate));
    }
}