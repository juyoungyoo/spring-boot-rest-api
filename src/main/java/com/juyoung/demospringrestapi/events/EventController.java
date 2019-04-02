package com.juyoung.demospringrestapi.events;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)   // producs : response
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

//    @Autowired : 이미 빈으로 등록된 경우 생략
    public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
         this.eventRepository = eventRepository;
         this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto){
        Event event = modelMapper.map(eventDto, Event.class);       // EventDto > Event class로 변환

        Event newEvent = this.eventRepository.save(event);
        // 1. Location URL 만들기
        // - HATEOS가 제공하는 linkTo(), MethodOn() 사용
        /*
            URI createUri = linkTo(methodOn(EventController.class).createEvent(null)).slash("{id}").toUri();
            return ResponseEntity.created(createUri).build();
        */
//        URI createUri = linkTo(EventController.class).slash("{id}").toUri();
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createUri).body(event);
    }




}
