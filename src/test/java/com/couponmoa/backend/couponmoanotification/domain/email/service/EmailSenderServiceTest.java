package com.couponmoa.backend.couponmoanotification.domain.email.service;

import com.couponmoa.backend.couponmoanotification.domain.email.dto.EmailDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        EmailDto dto = mock(EmailDto.class);
        given(dto.getEmailList()).willReturn(null);

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> emailSenderService.send(dto)
        );

        assertEquals("이메일을 전달받지 못했습니다.", thrown.getMessage());
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void 이메일_리스트_isEmpty() {
        EmailDto dto = mock(EmailDto.class);
        given(dto.getEmailList()).willReturn(Collections.emptyList());

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> emailSenderService.send(dto)
        );

        assertEquals("이메일을 전달받지 못했습니다.", thrown.getMessage());
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void 메일_전송_성공() {
        EmailDto dto = new EmailDto(List.of("test@test.com"), "subject", "content");

        emailSenderService.send(dto);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
