package com.couponmoa.backend.couponmoanotification.service;

import com.couponmoa.backend.couponmoanotification.dto.MessageQueueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendEmail(MessageQueueDto dto) {
        if (dto.getEmailList() == null || dto.getEmailList().isEmpty()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getEmailList().toArray(new String[0]));
        message.setSubject(dto.getSubject());
        message.setText(dto.getCouponName() + dto.getText());

        mailSender.send(message);
    }
}
