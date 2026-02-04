package com.kvstech.tickets.services.impl;

import com.kvstech.tickets.domain.entities.Ticket;
import com.kvstech.tickets.domain.entities.TicketStatusEnum;
import com.kvstech.tickets.domain.entities.TicketType;
import com.kvstech.tickets.domain.entities.User;
import com.kvstech.tickets.exceptions.TicketSoldOutException;
import com.kvstech.tickets.exceptions.TicketTypeNotFoundException;
import com.kvstech.tickets.exceptions.UserNotFoundException;
import com.kvstech.tickets.repositories.TicketRepository;
import com.kvstech.tickets.repositories.TicketTypeRepository;
import com.kvstech.tickets.repositories.UserRepository;
import com.kvstech.tickets.services.QrCodeService;
import com.kvstech.tickets.services.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final QrCodeService qrCodeService;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s not found", userId)));
        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId).orElseThrow(() -> new TicketTypeNotFoundException(String.format("TicketType with ID %s not found", ticketTypeId)));
        int purchasedTickets = ticketRepository.countByTicketTypeId(ticketType.getId());
        Integer totalAvailable = ticketType.getTotalAvailable();

        if(purchasedTickets + 1 > totalAvailable){
            throw new TicketSoldOutException();
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);

        return savedTicket;
    }
}
