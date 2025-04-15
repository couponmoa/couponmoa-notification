package com.couponmoa.backend.couponmoanotification.service;

import com.couponmoa.backend.couponmoanotification.dto.MessageQueueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendEmail(MessageQueueDto dto) {
        if (dto.getEmailList() == null || dto.getEmailList().isEmpty()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setBcc(dto.getEmailList().toArray(new String[0]));
        message.setSubject(dto.getSubject());
        message.setText(dto.getName() + dto.getText());

        mailSender.send(message);
        log.info("메일 전송 완료");
    }
}
