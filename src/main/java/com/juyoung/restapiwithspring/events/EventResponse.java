package com.juyoung.restapiwithspring.events;


import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.events.period.Period;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
class EventResponse {

    private int id;

    private String name;

    private String description;

    private Period enrollmentDate;

    private Period eventDate;

    private int limitOfEnrollment;

    private String location;

    private boolean offline;

    private int basePrice;

    private int maxPrice;

    private boolean free;

    private Account manager;

    private EventStatus eventStatus;

}
