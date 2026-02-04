package com.kvstech.tickets.services.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kvstech.tickets.domain.entities.QrCode;
import com.kvstech.tickets.domain.entities.QrCodeEnum;
import com.kvstech.tickets.domain.entities.Ticket;
import com.kvstech.tickets.exceptions.QrCodeGenerationException;
import com.kvstech.tickets.exceptions.QrCodeNotFoundException;
import com.kvstech.tickets.repositories.QrCodeRepository;
import com.kvstech.tickets.services.QrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeServiceImpl implements QrCodeService {

    private static final int QR_HEIGHT = 300;
    private static final int QR_WIDTH = 300;
    private final QRCodeWriter qrCodeWriter;
    private final QrCodeRepository qrCodeRepository;
    @Override
    public QrCode generateQrCode(Ticket ticket) {
        try{
            UUID uniqueId = UUID.randomUUID();
            String qrCodeImage = generateQrCodeImage(uniqueId);
            QrCode qrCode = new QrCode();
            qrCode.setId(uniqueId);
            qrCode.setStatus(QrCodeEnum.ACTIVE);
            qrCode.setValue(qrCodeImage);
            qrCode.setTicket(ticket);
            return qrCodeRepository.saveAndFlush(qrCode);
        } catch (WriterException | IOException ex) {
            throw new QrCodeGenerationException("Failed to genrate a QRCode", ex);
        }
    }

    @Override
    public byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId) {
        QrCode qrCode = qrCodeRepository.findByTicketAndTicketPurchaserId(ticketId, userId)
                .orElseThrow(QrCodeNotFoundException::new);
        try{
            return  Base64.getDecoder().decode(qrCode.getValue());
        } catch (IllegalArgumentException ex){
            log.error("Invalid base 64 QR Code for ticket id: {}",ticketId, ex);
            throw new QrCodeNotFoundException();
        }
    }

    private String generateQrCodeImage(UUID uniqueId) throws WriterException, IOException {
        BitMatrix bitMatrix = qrCodeWriter.encode(
                uniqueId.toString(),
                BarcodeFormat.QR_CODE,
                QR_WIDTH,
                QR_HEIGHT
        );

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }
}
