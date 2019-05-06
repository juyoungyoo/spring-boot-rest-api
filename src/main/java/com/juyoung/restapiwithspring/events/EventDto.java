package com.juyoung.restapiwithspring.events;


import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 12. Event 생성 API 구현하기 : 입력값 제한 [ DTO 활용 ]
 * JSON ignore annotaion 도 가능하나 너무 많은 애노테이션 사용함으로 분
 * @validation 도 추가 예정이기 때문에
 * 단점 : 중복
 * JSR 303
 * binding
 */
@Builder @NoArgsConstructor @AllArgsConstructor
@Setter @Getter
public class EventDto {
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
}
