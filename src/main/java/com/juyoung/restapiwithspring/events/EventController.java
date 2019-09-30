package com.juyoung.restapiwithspring.events;


import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.CurrentUser;
import com.juyoung.restapiwithspring.common.ErrorsResource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
@AllArgsConstructor
@Slf4j
public class EventController {

    private final EventRepository eventRepository;

    private final EventConverter eventConverter;

    private final EventService eventService;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createEvent(@RequestBody @Valid EventCreateUpdateDto eventDto,
                                      @CurrentUser Account currentUser) {

        Event event = eventService.create(currentUser, eventDto.toEntity());

        ControllerLinkBuilder selfLinkBuilder = linkTo(this.getClass()).slash(event.getId());
        URI createUri = selfLinkBuilder.toUri();
        EventResource resource = new EventResource(event);
        resource.add(linkTo(EventController.class).withRel("query-events"));
        resource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        resource.add(selfLinkBuilder.withRel("updateAccount-event"));

        return ResponseEntity.created(createUri).body(resource);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updateEvent(@PathVariable int id,
                                      @RequestBody @Valid EventCreateUpdateDto eventDto,
                                      @CurrentUser Account account) {
        Event event = eventConverter.convert(eventDto);
        event.updateAccount(account);
        eventService.update(id, event);

        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-updateAccount").withRel("profile"));
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
        if (account != null && event.getManager().equals(account)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("updateAccount-event"));
        }
        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity notFoundResponse() {
        return ResponseEntity.notFound().build();
    }

}
