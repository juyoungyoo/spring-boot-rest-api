package com.juyoung.demospringrestapi.events;


import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)   // producs : response
public class EventController {


    @PostMapping
    public ResponseEntity createEvent(@RequestBody Event event){
        // 1. Location URL 만들기
        // - HATEOS가 제공하는 linkTo(), MethodOn() 사용
//        URI createUri = linkTo(methodOn(EventController.class).createEvent(null)).slash("{id}").toUri();
//        return ResponseEntity.created(createUri).build();
        URI createUri = linkTo(EventController.class).slash("{id}").toUri();
        event.setId(1);
        return ResponseEntity.created(createUri).body(event);
    }




}
