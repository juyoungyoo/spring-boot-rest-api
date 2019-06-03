package com.juyoung.restapiwithspring.events;

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
            errors.reject("wrongPrices", "Prices is wrong.");
        }
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())||
            endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){
            errors.rejectValue("endEventDateTime","wrongCode", "EndEventDateTime  is wrong.");
        }
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        if(beginEventDateTime.isAfter(eventDto.getEndEventDateTime()) ||
            beginEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())||
            beginEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())){
            errors.rejectValue("beginEventDateTime", "wrongCode", "BeginEventDateTime is wrong.");
        }
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        if(closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())||
            closeEnrollmentDateTime.isAfter(eventDto.getEndEventDateTime())||
            closeEnrollmentDateTime.isAfter(eventDto.getBeginEventDateTime())){
            errors.rejectValue("closeEnrollmentDateTime", "wrongCode", "CloseEnrollmentDateTime is wrong.");
        }
    }
}
