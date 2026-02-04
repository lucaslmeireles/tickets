package com.kvstech.tickets.mappers;

import com.kvstech.tickets.domain.CreateEventRequest;
import com.kvstech.tickets.domain.CreateTicketTypeRequest;
import com.kvstech.tickets.domain.UpdateEventRequest;
import com.kvstech.tickets.domain.UpdateTicketTypeRequest;
import com.kvstech.tickets.domain.dtos.*;
import com.kvstech.tickets.domain.entities.Event;
import com.kvstech.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

    ListEventTicketTypeResponseDto toDto(TicketType ticketType);

    ListEventResponseDto toListEventResponseDto(Event event);

    GetEventDetailsResponseDto toEventDetailsResponseDto(Event event);

    GetEventTicketTypeResponseDto toEventTicketTypeResponseDto(TicketType ticketType);

    UpdateTicketTypeRequest fromDto(UpdateTicketTypeRequestDto dto);

    UpdateEventRequest fromDto(UpdateEventRequestDto dto);

    UpdateTicketTypeResponseDto toUpdateTicketTypeResponseDto(TicketType ticketType);

    UpdateEventResponseDto toUpdateEventResponseDto(Event event);

    ListPublishedEventResponseDto toListPublishedEventResponseDto(Event event);

    GetPublisehdEventDetailsResponseDto toGetPublisehdEventDetailsResponseDto(Event event);

    GetPublishedEventDetailsTicketTypeResponseDto toGetPublishedEventDetailsTicketTypeResponseDto(TicketType ticketType);
}

