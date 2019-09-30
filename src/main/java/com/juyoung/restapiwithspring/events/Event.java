package com.juyoung.restapiwithspring.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.AccountSerializer;
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
class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 20)
    private String name;

    @Column(length = 200)
    private String description;

    private LocalDateTime beginEnrollmentDateTime;

    private LocalDateTime closeEnrollmentDateTime;

    private LocalDateTime beginEventDateTime;

    private LocalDateTime endEventDateTime;

    private int limitOfEnrollment;

    private String location; // (optional) 이게 없으면 온라인 모임

    private boolean offline;

    private int basePrice; // (optional)

    private int maxPrice;

    private boolean free;

    @CreatedDate
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    private LocalDateTime lastModifiedDateTime;

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
        this.beginEnrollmentDateTime = event.beginEnrollmentDateTime;
        this.closeEnrollmentDateTime = event.closeEnrollmentDateTime;
        this.beginEventDateTime = event.closeEnrollmentDateTime;
        this.endEventDateTime = event.endEventDateTime;
        this.location = event.location;
        this.basePrice = event.basePrice;
        this.maxPrice = event.maxPrice;
        this.limitOfEnrollment = event.limitOfEnrollment;

        updateStatus();
    }

    void updateStatus() {
        if (this.basePrice > 0 || this.maxPrice > 0) {
            this.free = false;
        } else {
            this.free = true;
        }

        if (this.location == null || this.location.trim().isEmpty()) {
            this.offline = false;
        } else {
            this.offline = true;
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", beginEnrollmentDateTime=" + beginEnrollmentDateTime +
                ", closeEnrollmentDateTime=" + closeEnrollmentDateTime +
                ", beginEventDateTime=" + beginEventDateTime +
                ", endEventDateTime=" + endEventDateTime +
                ", limitOfEnrollment=" + limitOfEnrollment +
                ", location='" + location + '\'' +
                ", offline=" + offline +
                ", basePrice=" + basePrice +
                ", maxPrice=" + maxPrice +
                ", free=" + free +
                ", createdDateTime=" + createdDateTime +
                ", lastModifiedDateTime=" + lastModifiedDateTime +
                ", manager=" + manager +
                ", eventStatus=" + eventStatus +
                '}';
    }
}
