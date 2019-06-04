package com.juyoung.restapiwithspring.events;


import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.CurrentUser;
import com.juyoung.restapiwithspring.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)   // producs : response
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository,
                           ModelMapper modelMapper,
                           EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable int id,
                                      @RequestBody @Valid EventDto.CreateOrUpdate eventDto, Errors errors,
                                      @CurrentUser Account account) {
        if (errors.hasErrors()) {
            return badRequestResponse(errors);
        }

        Optional<Event> byId = eventRepository.findById(id);
        if (!byId.isPresent()) {
            return notFoundResponse();
        }

        Event event = byId.get();
        if(!event.getManager().equals(account)){
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        modelMapper.map(eventDto, event);
        event.update();
        eventValidator.validate(event, errors);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }
        Event newEvent = eventRepository.save(event);

        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(@CurrentUser Account account,
                                      Pageable pageable,
                                      PagedResourcesAssembler assembler) {
        Page<Event> page = eventRepository.findAll(pageable);

        PagedResources pagedResources = assembler.toResource(page, e -> new EventResource((Event) e));
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        if (account != null) {
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable int id, @CurrentUser Account account) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (!optionalEvent.isPresent()) {
            return notFoundResponse();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if(account != null && event.getManager().equals(account)){
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto.CreateOrUpdate eventDto, Errors errors,
                                      @CurrentUser Account currentUser) {    // errors : java bean spec을 따르지 않는다.
        if (errors.hasErrors()) {
            return badRequestResponse(errors);
        }
        Event event = modelMapper.map(eventDto, Event.class);
        event.update(currentUser);

        eventValidator.validate(event, errors);
        if (errors.hasErrors()) {
            return badRequestResponse(errors);
        }

        Event newEvent = this.eventRepository.save(event);

        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();

        EventResource resource = new EventResource(event);
        resource.add(linkTo(EventController.class).withRel("query-events"));
        resource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        resource.add(selfLinkBuilder.withRel("update-event"));
        return ResponseEntity.created(createUri).body(resource);
    }

    private ResponseEntity notFoundResponse() {
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity badRequestResponse(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
