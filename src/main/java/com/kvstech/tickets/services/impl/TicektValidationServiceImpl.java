package com.kvstech.tickets.services.impl;

import com.kvstech.tickets.domain.entities.*;
import com.kvstech.tickets.exceptions.QrCodeNotFoundException;
import com.kvstech.tickets.exceptions.TicketNotFoundException;
import com.kvstech.tickets.repositories.QrCodeRepository;
import com.kvstech.tickets.repositories.TicketRepository;
import com.kvstech.tickets.repositories.TicketValidationRepository;
import com.kvstech.tickets.services.TicketValidationSerivce;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicektValidationServiceImpl implements TicketValidationSerivce{

    private final QrCodeRepository qrCodeRepository;
    private final TicketValidationRepository ticketValidationRepository;
    private final TicketRepository ticketRepository;


    @Override
    public TicketValidation validateTicketByQrCode(UUID qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeEnum.ACTIVE)
                .orElseThrow(()-> new QrCodeNotFoundException(String.format("QR Code with ID %s was not found", qrCodeId)));
        Ticket ticket = qrCode.getTicket();
        TicketValidation ticketValidation = validateTicket(ticket);

        return ticketValidationRepository.save(ticketValidation);
    }

    private static TicketValidation validateTicket(Ticket ticket) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setTicket(ticket);
        ticketValidation.setValidationMethod(TicketValidationMethodEnum.QR_SCAN);
        TicketValidationEnum ticketValidationEnum = ticket.getValidations().stream()
                .filter(v -> TicketValidationEnum.VALID.equals(v.getStatus()))
                .findFirst()
                .map(v -> TicketValidationEnum.INVALID)
                .orElse(TicketValidationEnum.VALID);
        ticketValidation.setStatus(ticketValidationEnum);
        return ticketValidation;
    }

    @Override
    public TicketValidation validateTicketManually(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(TicketNotFoundException::new);
        return validateTicket(ticket);
    }
}
