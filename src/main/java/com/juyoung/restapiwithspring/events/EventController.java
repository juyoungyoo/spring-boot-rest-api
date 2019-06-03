package com.juyoung.restapiwithspring.events;


import com.juyoung.restapiwithspring.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;               // **
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler assembler){
        Page<Event> page = eventRepository.findAll(pageable);
        PagedResources pagedResources = assembler.toResource(page, e -> new EventResource((Event) e));
        pagedResources.add(new Link("/docs/index.html#resources-query-events").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }


    // @Valid와 BindingResult (or Error)
    // : dto에 바인딩할 때, 검증을 할 수 있다.
    // BindingResult는 항상 @Valid 바로 다음 인자로 사용해야 한다. ( 스프링 MVC )
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){    // errors : java bean spec을 따르지 않는다.
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        Event event = modelMapper.map(eventDto, Event.class);

        event.update();
        Event newEvent = this.eventRepository.save(event);

        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();
        EventResource resource = new EventResource(event);
        resource.add(linkTo(EventController.class).withRel("query-events"));
        resource.add(selfLinkBuilder.withRel("update-event"));
        resource.add(new Link("/docs/index.html").withRel("profile"));
        return ResponseEntity.created(createUri).body(resource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
