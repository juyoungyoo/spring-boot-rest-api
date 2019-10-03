package com.juyoung.restapiwithspring.events;

import com.juyoung.restapiwithspring.events.period.Period;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
class EventCreateUpdateDto {

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    private Period enrollmentDate;

    @NotNull
    private Period eventDate;

    private String location;

    @Min(0)
    private int basePrice;

    @Min(0)
    private int maxPrice;

    @Min(0)
    private int limitOfEnrollment;

    Event toEntity() {
        return Event.builder()
                .name(name)
                .description(description)
                .enrollmentDate(enrollmentDate)
                .eventDate(eventDate)
                .location(location)
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .limitOfEnrollment(limitOfEnrollment)
                .build();
    }
}
