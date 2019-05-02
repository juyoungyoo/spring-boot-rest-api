package com.juyoung.demospringrestapi.events;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


// @Data 사용안하는 이유 : @Entity에서 사용하면 안됨 EqualsAndHashCode 시 상호관계로 StackOfFlow 발생할 수 있다.

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = {"id", "name"}) // 객체간의 엔티티를 참조할 때 상호참조 때문에 해당하는 필드는 사용하지 X
@Entity
public class Event {

    @Id @GeneratedValue
    private int id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice;
    private int limitOfEnrollment;

    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void update() {
        // Update free
        if(this.basePrice ==0 && this.maxPrice == 0){
            this.free = true;
        }else{
            this.free = false;
        }

        // Update Online
        if(this.location == null || this.location.trim().isEmpty()){
            this.offline = false;
        }else{
            this.offline = true;
        }
    }
}
