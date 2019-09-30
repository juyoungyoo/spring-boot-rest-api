package com.juyoung.restapiwithspring.events;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    private LocalDateTime beginEnrollmentDateTime;

    @NotNull
    private LocalDateTime closeEnrollmentDateTime;

    @NotNull
    private LocalDateTime beginEventDateTime;

    @NotNull
    private LocalDateTime endEventDateTime;

    private String location; // (optional) 이게 없으면 온라인 모임

    @Min(0)
    private int basePrice; // (optional)

    @Min(0)
    private int maxPrice;

    @Min(0)
    private int limitOfEnrollment;

    Event toEntity() {
        return Event.builder()
                .name(name)
                .description(description)
                .beginEnrollmentDateTime(beginEnrollmentDateTime)
                .closeEnrollmentDateTime(closeEnrollmentDateTime)
                .beginEventDateTime(beginEventDateTime)
                .endEventDateTime(endEventDateTime)
                .location(location)
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .limitOfEnrollment(limitOfEnrollment)
                .build();
    }
}
