package com.juyoung.restapiwithspring.events;

import com.juyoung.restapiwithspring.accounts.Account;
import com.juyoung.restapiwithspring.accounts.NotMatchAccountException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    Event create(Account currentUser, Event event) {
        event.create(currentUser);
        return eventRepository.save(event);
    }
}
