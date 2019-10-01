package com.juyoung.restapiwithspring.events;


import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.CurrentUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
@AllArgsConstructor
@Slf4j
public class EventController {

    private final EventConverter eventConverter;

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createEvent(@RequestBody @Valid EventCreateUpdateDto eventDto,
                                      @CurrentUser Account currentUser) {

        Event event = eventService.create(currentUser, eventDto.toEntity());
        EventResponse eventResponse = eventConverter.convert(event);

        ControllerLinkBuilder selfLinkBuilder = linkTo(this.getClass()).slash(event.getId());
        URI createUri = selfLinkBuilder.toUri();
        EventResource resource = new EventResource(eventResponse);
        resource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        resource.add(selfLinkBuilder.withRel("update-account-event"));

        return ResponseEntity.created(createUri).body(resource);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity queryEvents(@CurrentUser Account account,
                                      Pageable pageable,
                                      PagedResourcesAssembler<EventResponse> assembler) {
        Page<Event> events = eventService.query(pageable);

        PageImpl<EventResponse> postResponses = new PageImpl<>(events.stream()
                .map(eventConverter::convert)
                .collect(Collectors.toList()),
                pageable, events.getTotalElements());

        PagedResources<EventResource> resources = assembler.toResource(postResponses, EventResource::new);
        resources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        if (Objects.nonNull(account)) {
            resources.add(linkTo(this.getClass()).withRel("create-event"));
        }
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity readEvent(@PathVariable int id,
                                    @CurrentUser Account account) {
        Event event = eventService.read(id);

        EventResource resource = new EventResource(eventConverter.convert(event));
        resource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if (Objects.nonNull(account) && event.isMatchManager(account)) {
            resource.add(linkTo(EventController.class)
                    .slash(event.getId())
                    .withRel("update-account-event"));
        }
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updateEvent(@PathVariable int id,
                                      @RequestBody @Valid EventCreateUpdateDto eventDto,
                                      @CurrentUser Account account) {
        Event event = eventService.update(id, eventDto.toEntity(), account);

        EventResource resource = new EventResource(eventConverter.convert(event));
        resource.add(new Link("/docs/index.html#resources-events-updateAccount").withRel("profile"));
        return ResponseEntity.ok(resource);
    }
}