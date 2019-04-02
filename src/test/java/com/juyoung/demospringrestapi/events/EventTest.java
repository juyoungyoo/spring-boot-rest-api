package com.juyoung.demospringrestapi.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}