package com.juyoung.demospringrestapi.events;


import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

/**
 * 12. Event 생성 API 구현하기 : 입력값 제한 [ DTO 활용 ]
 * JSON ignore annotaion 도 가능하나 너무 많은 애노테이션 사용함으로 분
 * @validation 도 추가 예정이기 때문에
 * 단점 : 중복
 */
@Builder @NoArgsConstructor @AllArgsConstructor
@Setter @Getter
public class EventDto {
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
}
