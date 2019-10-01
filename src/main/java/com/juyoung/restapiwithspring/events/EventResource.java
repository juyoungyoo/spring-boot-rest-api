package com.juyoung.restapiwithspring.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


class EventResource extends Resource<EventResponse> {

    EventResource(EventResponse response, Link... links) {
        super(response, links);
        add(linkTo(EventController.class).withRel("query-events"));
        add(linkTo(EventController.class).slash(response.getId()).withSelfRel());
    }
}