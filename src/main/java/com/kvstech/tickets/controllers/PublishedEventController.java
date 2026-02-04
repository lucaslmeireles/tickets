package com.kvstech.tickets.controllers;


import com.kvstech.tickets.domain.dtos.GetPublisehdEventDetailsResponseDto;
import com.kvstech.tickets.domain.dtos.ListPublishedEventResponseDto;
import com.kvstech.tickets.domain.entities.Event;
import com.kvstech.tickets.mappers.EventMapper;
import com.kvstech.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/published-events")
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<ListPublishedEventResponseDto>> listPublishedEvents(
            @RequestParam(required = false) String q,
            Pageable pageable){
        Page<Event> events;
        if(null != q && !q.trim().isEmpty()){
            events = eventService.serachPublishedEvents(q, pageable);
        } else {
            events = eventService.listPublishedEvent(pageable);
        }
        return ResponseEntity.ok(events.map(eventMapper::toListPublishedEventResponseDto));
    }

    @GetMapping(path = "/{event_id}")
    public  ResponseEntity<GetPublisehdEventDetailsResponseDto> getPublishedEventDetais(
            @PathVariable UUID eventId
    ){

      return eventService.getPublishedEvent(eventId)
              .map(eventMapper::toGetPublisehdEventDetailsResponseDto)
              .map(ResponseEntity::ok)
              .orElse(ResponseEntity.notFound().build());
    }

}
