package com.kvstech.tickets.services.impl;

import com.kvstech.tickets.domain.CreateEventRequest;
import com.kvstech.tickets.domain.UpdateEventRequest;
import com.kvstech.tickets.domain.UpdateTicketTypeRequest;
import com.kvstech.tickets.domain.entities.Event;
import com.kvstech.tickets.domain.entities.EventStatusEnum;
import com.kvstech.tickets.domain.entities.TicketType;
import com.kvstech.tickets.domain.entities.User;
import com.kvstech.tickets.exceptions.EventNotFoundException;
import com.kvstech.tickets.exceptions.EventUpdateException;
import com.kvstech.tickets.exceptions.TicketTypeNotFoundException;
import com.kvstech.tickets.exceptions.UserNotFoundException;
import com.kvstech.tickets.repositories.EventRepository;
import com.kvstech.tickets.repositories.UserRepository;
import com.kvstech.tickets.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    @Override
    @Transactional
    public Event createEvent(UUID organizerId, CreateEventRequest event) {
        User organizer = userRepository.findById(organizerId).orElseThrow(() ->
                new UserNotFoundException(String.format("User with IID %s not found", organizerId)));
        Event eventToCreate = new Event();

        List<TicketType> ticketTypes = event.getTicketTypes().stream().map(ticketType -> {
            TicketType ticketTypeCreate = new TicketType();
            ticketTypeCreate.setName(ticketType.getName());
            ticketTypeCreate.setPrice(ticketType.getPrice());
            ticketTypeCreate.setDescription(ticketType.getDescription());
            ticketTypeCreate.setTotalAvailable(ticketType.getTotalAvailable());
            ticketTypeCreate.setEvent(eventToCreate);
            return ticketTypeCreate;
        }).toList();

        eventToCreate.setName(event.getName());
        eventToCreate.setStart(event.getStart());
        eventToCreate.setEnd(event.getEnd());
        eventToCreate.setStatus(event.getStatus());
        eventToCreate.setVenue(event.getVenue());
        eventToCreate.setSalesStart(event.getSalesStart());
        eventToCreate.setSalesEnd(event.getSalesEnd());
        eventToCreate.setOrganizer(organizer);
        eventToCreate.setTicketTypes(ticketTypes);

        return eventRepository.save(eventToCreate);
    }

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest event) {
        if(null == event.getId()){
            throw new EventUpdateException("Event id can not be null");
        }
        if(!id.equals(event.getId())){
            throw new EventUpdateException("Cannot update the ID of the event");
        }
        Event existingEvent = eventRepository.findByIdAndOrganizerId(id,organizerId)
                .orElseThrow(()-> new EventNotFoundException(String.format("Event with ID %s does not exists", id)));

        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());

        Set<UUID> requestTicketTypesIds = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent.getTicketTypes().removeIf(existingTicketType ->
                !requestTicketTypesIds.contains(existingTicketType.getId()));

        Map<UUID, TicketType> existingTicketTypeIndex = existingEvent.getTicketTypes().stream().collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for (UpdateTicketTypeRequest ticketType : event.getTicketTypes()){
            if(null == ticketType.getId()){
                TicketType ticketTypeToCreate = new TicketType();
                ticketTypeToCreate.setName(ticketType.getName());
                ticketTypeToCreate.setPrice(ticketType.getPrice());
                ticketTypeToCreate.setDescription(ticketType.getDescription());
                ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                ticketTypeToCreate.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(ticketTypeToCreate);
            } else if (existingTicketTypeIndex.containsKey(ticketType.getId())) {
                TicketType existingTicketType = existingTicketTypeIndex.get(ticketType.getId());
                existingTicketType.setName(ticketType.getName());
                existingTicketType.setPrice(ticketType.getPrice());
                existingTicketType.setDescription(ticketType.getDescription());
                existingTicketType.setTotalAvailable(ticketType.getTotalAvailable());

            } else {
                throw new TicketTypeNotFoundException(
                        String.format("Ticket with ID %s does not exists", ticketType.getId())
                );
            }
        }

        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void deleteEventForOrganizer(UUID organizerId, UUID id) {
        Event eventDeleted = eventRepository.findByIdAndOrganizerId(id, organizerId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        eventRepository.delete(eventDeleted);
        //getEventForOrganizer(organizerId, id).ifPresent(eventRepository::delete);
    }

    @Override
    public Page<Event> listPublishedEvent(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }

    @Override
    public Page<Event> serachPublishedEvents(String querry, Pageable pageable) {
        return eventRepository.searchEvents(querry, pageable);
    }

    @Override
    public Optional<Event> getPublishedEvent(UUID id) {
        return eventRepository.findByIdAndStatus(id, EventStatusEnum.PUBLISHED);
    }
}
