package com.juyoung.restapiwithspring.events;


import com.juyoung.restapiwithspring.accounts.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
class EventResponse {

    private int id;

    private String name;

    private String description;

    private LocalDateTime beginEnrollmentDateTime;

    private LocalDateTime closeEnrollmentDateTime;

    private LocalDateTime beginEventDateTime;

    private LocalDateTime endEventDateTime;

    private int limitOfEnrollment;

    private String location;

    private boolean offline;

    private int basePrice;

    private int maxPrice;

    private boolean free;

    private Account manager;

    private EventStatus eventStatus;

}
