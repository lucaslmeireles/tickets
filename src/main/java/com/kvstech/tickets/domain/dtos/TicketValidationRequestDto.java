package com.kvstech.tickets.domain.dtos;

import com.kvstech.tickets.domain.entities.TicketValidationEnum;
import com.kvstech.tickets.domain.entities.TicketValidationMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketValidationRequestDto {
    private UUID id;
    private TicketValidationMethodEnum method;

}
