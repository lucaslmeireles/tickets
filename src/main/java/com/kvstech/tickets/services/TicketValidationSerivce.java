package com.kvstech.tickets.services;

import com.kvstech.tickets.domain.entities.TicketValidation;

import java.util.UUID;

public interface TicketValidationSerivce {
    TicketValidation validateTicketByQrCode(UUID qrCodeId);
    TicketValidation validateTicketManually(UUID ticketId);
}

