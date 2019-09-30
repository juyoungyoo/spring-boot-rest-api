package com.juyoung.restapiwithspring.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @DisplayName("이벤트 참가비가 없을 시 무료")
    @ParameterizedTest
    @CsvSource({
            "0, 0, true",
            "100, 0, false",
            "0, 100, false",
    })
    void updateStatus_whenPriceIsZero_thenIsFree(int basePrice, int maxPrice, boolean expectedIsFree) {
        // given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // when
        event.updateStatus();

        // then
        assertThat(event.isFree()).isEqualTo(expectedIsFree);
    }

    // todo : null check
    @DisplayName("위치값이 없을 시 online 이며 아닐 시 offline")
    @ParameterizedTest
    @CsvSource({
            "강남역 네이버 D2 스타텁 팩토리, true",
            "' ', false",
    })
    void updateStatus_whenLocationIsNull_thenOnline(String location, boolean expectedOfOffline) {
        // Given
        Event event = Event.builder()
                .location(location)
                .build();
        // When
        event.updateStatus();

        // Then
        assertThat(event.isOffline()).isEqualTo(expectedOfOffline);
    }
}
