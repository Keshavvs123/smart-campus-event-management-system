package com.smcem.service;

import com.smcem.entity.Event;
import com.smcem.entity.EventStatus;
import com.smcem.entity.User;
import com.smcem.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event saveEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        validateEvent(event);
        return eventRepository.save(event);
    }

    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> findById(Long id) {        if (id == null) {
            return Optional.empty();
        }        return eventRepository.findById(id);
    }

    public List<Event> findByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }

    public List<Event> findByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    public void updateEventStatuses() {
        LocalDate today = LocalDate.now();
        List<Event> eventsToClose = eventRepository.findEventsToClose(today);
        for (Event event : eventsToClose) {
            event.setStatus(EventStatus.CLOSED);
            eventRepository.save(event);
        }

        List<Event> eventsToComplete = eventRepository.findEventsToComplete(today);
        for (Event event : eventsToComplete) {
            event.setStatus(EventStatus.COMPLETED);
            eventRepository.save(event);
        }
    }

    private void validateEvent(Event event) {
        LocalDate today = LocalDate.now();
        if (event.getDate().isBefore(today)) {
            throw new IllegalArgumentException("Event date cannot be in the past");
        }
        if (event.getDate().equals(today) && event.getTime().isBefore(java.time.LocalTime.now())) {
            throw new IllegalArgumentException("Event time must be in the future if today");
        }
        if (event.getRegistrationDeadline().isAfter(event.getDate())) {
            throw new IllegalArgumentException("Registration deadline must be before or on event date");
        }
    }
}