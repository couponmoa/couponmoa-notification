package com.couponmoa.backend.couponmoanotification.listener;

import com.couponmoa.backend.couponmoanotification.dto.MessageQueueDto;
import com.couponmoa.backend.couponmoanotification.service.EmailSenderService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertMessageListener {

    private final EmailSenderService emailSenderService;

    @SqsListener("couponmoa-queue")
    public void receiveMQ(@Payload MessageQueueDto message) {
        emailSenderService.sendEmail(message);
    }
}
