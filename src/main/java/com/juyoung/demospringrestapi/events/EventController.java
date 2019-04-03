package com.juyoung.demospringrestapi.events;


import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;               // **
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)   // producs : response
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
         this.eventRepository = eventRepository;
         this.modelMapper = modelMapper;
         this.eventValidator =  eventValidator;
    }

    // @Valid와 BindingResult (or Error)
    // : dto에 바인딩할 때, 검증을 할 수 있다.
    // BindingResult는 항상 @Valid 바로 다음 인자로 사용해야 한다. ( 스프링 MVC )
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){    // errors : java bean spec을 따르지 않는다.

        if(errors.hasErrors()){
//            return ResponseEntity.badRequest().build();
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
//            return ResponseEntity.badRequest().build();
            return ResponseEntity.badRequest().body(errors);
        }
        Event event = modelMapper.map(eventDto, Event.class);       // EventDto > Event class로 변환

        Event newEvent = this.eventRepository.save(event);
        // 1. Location URL 만들기
        // - HATEOS가 제공하는 linkTo(), MethodOn() 사용
        /*
            URI createUri = linkTo(methodOn(EventController.class).createEvent(null)).slash("{id}").toUri();
            return ResponseEntity.created(createUri).build();
        */
//        URI createUri = linkTo(EventController.class).slash("{id}
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createUri).body(event);
    }




}
