package com.kvstech.tickets.controllers;

import com.kvstech.tickets.domain.dtos.TicketValidationRequestDto;
import com.kvstech.tickets.domain.dtos.TicketValidationResponseDto;
import com.kvstech.tickets.domain.entities.TicketValidation;
import com.kvstech.tickets.domain.entities.TicketValidationMethodEnum;
import com.kvstech.tickets.mappers.TicketValidationMapper;
import com.kvstech.tickets.services.TicketValidationSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ticket-validations")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationSerivce ticketValidationSerivce;
    private final TicketValidationMapper ticketValidationMapper;

    @PostMapping
    public ResponseEntity<TicketValidationResponseDto> validateTicket(
            @RequestBody TicketValidationRequestDto ticketValidationRequestDto
            ){
        TicketValidationMethodEnum method = ticketValidationRequestDto.getMethod();
        TicketValidation ticketValidation;
        if(TicketValidationMethodEnum.MANUAL.equals(method)){
            ticketValidation  = ticketValidationSerivce.validateTicketManually(ticketValidationRequestDto.getId());
        } else {
            ticketValidation = ticketValidationSerivce.validateTicketByQrCode(ticketValidationRequestDto.getId());
        }
        return ResponseEntity.ok(
                ticketValidationMapper.toTicketValidationResponseDto(ticketValidation)
        );
    }

}
