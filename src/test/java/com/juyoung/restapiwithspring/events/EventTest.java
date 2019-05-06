package com.juyoung.restapiwithspring.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("inflearn Spring REST API")
                .description("REST API developer with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // given
        String name = "event";
        String description = "Spring";

        // when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);

    }

    @Test
    @Parameters
//    @Parameters(method ="parametersForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        event.update();
//        assertThat(event.isFree()).isTrue();
        assertThat(event.isFree()).isEqualTo(isFree);
    }
    // prefix : paramtersFor
    private Object[] parametersForTestFree(){
        return new Object[]{
            new Object[]{0, 0, true},
            new Object[]{100, 0, false},
            new Object[]{0, 100, false}
        };
    }

    @Test
    @Parameters
    public void testOnline(String location, boolean isOffline) {
        // Given
        Event event = Event.builder()
                .location(location)
                .build();
        // When
        event.update();
        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private Object[] parametersForTestOnline(){
        return new Object[]{
                new Object[]{"강남역 네이버 D2 스타텁 팩토리", true},
                new Object[]{null, false},
                new Object[]{"      ", false}
        };
    }
}