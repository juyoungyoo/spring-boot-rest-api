package com.juyoung.restapiwithspring.events;

import com.juyoung.restapiwithspring.global.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EventConverter implements Converter<Event, EventResponse> {

    @Override
    public EventResponse convert(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .beginEnrollmentDateTime(event.getBeginEnrollmentDateTime())
                .closeEnrollmentDateTime(event.getCloseEnrollmentDateTime())
                .beginEventDateTime(event.getBeginEventDateTime())
                .endEventDateTime(event.getEndEventDateTime())
                .limitOfEnrollment(event.getLimitOfEnrollment())
                .location(event.getLocation())
                .offline(event.isOffline())
                .basePrice(event.getBasePrice())
                .maxPrice(event.getBasePrice())
                .free(event.isFree())
                .manager(event.getManager())
                .eventStatus(event.getEventStatus())
                .build();
    }
}