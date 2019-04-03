package com.juyoung.demospringrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors){
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0 ){
            errors.rejectValue("basePrice","wrongCode", "BasePrice is wrong.");
            errors.rejectValue("maxPrice","wrongCode", "MaxPrice is wrong.");
        }
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||      // 시작 전
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())||
            endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){ // 접수 종료전이거
            errors.rejectValue("endEventDateTime","wrongCode", "EndEventDateTime  is wrong.");
        }

        // TODO beginEventDateTime
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        if(beginEventDateTime.isAfter(eventDto.getEndEventDateTime()) ||
            beginEventDateTime.isAfter(eventDto.getBeginEnrollmentDateTime())||
            beginEventDateTime.isAfter(eventDto.getCloseEnrollmentDateTime())){
            errors.rejectValue("beginEventDateTime", "wrongCode", "BeginEventDateTime is wrong.");
        }

        // TODO CloseEnrollmentDateTime
        // 등록 닫힘 시간
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        if(closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())||
            closeEnrollmentDateTime.isAfter(eventDto.getEndEventDateTime())||
            closeEnrollmentDateTime.isBefore(eventDto.getBeginEventDateTime())){
            errors.rejectValue("closeEnrollmentDateTime", "wrongCode", "CloseEnrollmentDateTime is wrong.");
        }
    }
}
