package com.kvstech.tickets.services;

import com.kvstech.tickets.domain.entities.QrCode;
import com.kvstech.tickets.domain.entities.Ticket;

import java.util.UUID;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);
    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}
