package com.juyoung.restapiwithspring.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Event.class.equals(clazz);
    }

    @Override
    public void validate(Object target,
                         Errors errors) {
        Event event = (Event) target;
        validatePrice(event, errors);
        validateBeginEventDateTime(event, errors);
        validateEndEventDateTime(event, errors);
        validateCloseEnrollmentDateTime(event, errors);
    }

    private void validatePrice(Event event,
                               Errors errors) {
        if (event.getBasePrice() > event.getMaxPrice() && event.getMaxPrice() > 0) {
            errors.rejectValue("basePrice", "wrongCode", "BasePrice is wrong.");
            errors.rejectValue("maxPrice", "wrongCode", "MaxPrice is wrong.");
            errors.reject("wrongPrices", "Prices is wrong.");
        }
    }

    private void validateBeginEventDateTime(Event event,
                                            Errors errors) {
        LocalDateTime beginEventDateTime = event.getBeginEventDateTime();
        if (beginEventDateTime.isAfter(event.getEndEventDateTime()) ||
                beginEventDateTime.isBefore(event.getBeginEnrollmentDateTime()) ||
                beginEventDateTime.isBefore(event.getCloseEnrollmentDateTime())) {
            errors.rejectValue("beginEventDateTime", "wrongCode", "BeginEventDateTime is wrong.");
        }
    }

    private void validateCloseEnrollmentDateTime(Event event,
                                                 Errors errors) {
        LocalDateTime closeEnrollmentDateTime = event.getCloseEnrollmentDateTime();
        if (closeEnrollmentDateTime.isBefore(event.getBeginEnrollmentDateTime()) ||
                closeEnrollmentDateTime.isAfter(event.getEndEventDateTime()) ||
                closeEnrollmentDateTime.isAfter(event.getBeginEventDateTime())) {
            errors.rejectValue("closeEnrollmentDateTime", "wrongCode", "CloseEnrollmentDateTime is wrong.");
        }
    }

    private void validateEndEventDateTime(Event event,
                                          Errors errors) {
        LocalDateTime endEventDateTime = event.getEndEventDateTime();
        if (endEventDateTime.isBefore(event.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(event.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(event.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongCode", "EndEventDateTime  is wrong.");
        }
    }

}
