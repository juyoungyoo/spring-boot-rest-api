package com.juyoung.restapiwithspring.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountSerializer;
import com.juyoung.restapiwithspring.events.period.Period;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "name"})
@ToString
class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 20)
    private String name;

    @Column(length = 200)
    private String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "begin_enrollment_date")),
            @AttributeOverride(name = "endDate", column = @Column(name = "close_enrollment_date"))
    })
    private Period enrollmentDate;

    @Embedded
    @AttributeOverride(name = "startDate", column = @Column(name = "begin_event_date"))
    @AttributeOverride(name = "endDate", column = @Column(name = "close_event_date"))
    private Period eventDate;

    private int limitOfEnrollment;

    private String location; // (optional) 이게 없으면 온라인 모임

    private boolean offline;

    private int basePrice; // (optional)

    private int maxPrice;

    private boolean free;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EventStatus eventStatus = EventStatus.DRAFT;

    void create(Account currentUser) {
        this.manager = currentUser;
        updateStatus();
    }

    boolean isMatchManager(Account manager) {
        return manager.equals(manager);
    }

    void update(Event event) {
        this.name = event.name;
        this.description = event.description;
        this.enrollmentDate = event.enrollmentDate;
        this.eventDate = event.eventDate;
        this.location = event.location;
        this.basePrice = event.basePrice;
        this.maxPrice = event.maxPrice;
        this.limitOfEnrollment = event.limitOfEnrollment;

        updateStatus();
    }

    void updateStatus() {
        this.free = (this.basePrice <= 0) && (this.maxPrice <= 0);

        this.offline = (this.location != null) && (!this.location.trim().isEmpty());
    }
}
