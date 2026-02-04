package com.kvstech.tickets.repositories;

import com.kvstech.tickets.domain.entities.QrCode;
import com.kvstech.tickets.domain.entities.QrCodeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QrCodeRepository  extends JpaRepository<QrCode, UUID> {
    Optional<QrCode> findByTicketAndTicketPurchaserId(UUID ticketId, UUID ticketPurchserId);
    Optional<QrCode> findByIdAndStatus(UUID id, QrCodeEnum status);
}
