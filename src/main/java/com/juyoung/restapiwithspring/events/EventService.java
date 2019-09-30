package com.juyoung.restapiwithspring.events;

import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.NotMatchAccountException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    Page<Event> query(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    Event read(int id) {
        return eventRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    Event create(Account currentUser, Event event) {
        event.create(currentUser);
        return eventRepository.save(event);
    }

    Event update(int id, Event updateEvent, Account account) {
        Event event = eventRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        if(!event.isMatchManager(account)){
            throw new NotMatchAccountException();
        }
        event.update(updateEvent);
        return eventRepository.save(event);
    }
}
