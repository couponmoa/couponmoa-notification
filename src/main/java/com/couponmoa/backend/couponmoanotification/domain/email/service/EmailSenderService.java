package com.couponmoa.backend.couponmoanotification.domain.email.service;

import com.couponmoa.backend.couponmoanotification.domain.email.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailDto dto) {
        List<String> emailList = dto.getEmailList();
        if (emailList == null || emailList.isEmpty()) {
            throw new IllegalStateException("이메일을 전달받지 못했습니다.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setBcc(emailList.toArray(new String[0]));
        message.setSubject(dto.getSubject());
        message.setText(dto.getContent());

        mailSender.send(message);
    }
}
