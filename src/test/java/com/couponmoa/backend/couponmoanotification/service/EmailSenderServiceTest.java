package com.couponmoa.backend.couponmoanotification.service;

import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponCreateMessage;
import com.couponmoa.backend.couponmoanotification.domain.email.service.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailSenderServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @Test
    void 이메일_리스트_null() {
        CouponCreateMessage dto = mock(CouponCreateMessage.class);
        given(dto.getEmailList()).willReturn(null);
        emailSenderService.sendEmail(dto);
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void 이메일_리스트_isEmpty() {
        CouponCreateMessage dto = mock(CouponCreateMessage.class);
        given(dto.getEmailList()).willReturn(Collections.emptyList());
        emailSenderService.sendEmail(dto);
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void 메일_전송_성공() {
        CouponCreateMessage dto = new CouponCreateMessage(
                Arrays.asList("test@test.com", "test1@test.com"), "subject", "text", "name");
        emailSenderService.sendEmail(dto);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
