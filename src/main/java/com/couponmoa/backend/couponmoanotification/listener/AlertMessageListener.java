package com.couponmoa.backend.couponmoanotification.listener;

import com.couponmoa.backend.couponmoanotification.dto.CouponAlertDto;
import com.couponmoa.backend.couponmoanotification.dto.MessageQueueDto;
import com.couponmoa.backend.couponmoanotification.infra.SseEmitterManager;
import com.couponmoa.backend.couponmoanotification.service.EmailSenderService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class AlertMessageListener {

    private final EmailSenderService emailSenderService;
    private final SseEmitterManager sseEmitterManager;

    @SqsListener("couponmoa-queue")
    public void receiveMQ(@Payload MessageQueueDto message) {
        emailSenderService.sendEmail(message);
    }

    // userId에 저장된 emitter 찾아서 알림 전송
    @SqsListener("coupon-alert-queue")
    public void receiveCouponMQ(@Payload CouponAlertDto alertDto) {
        sseEmitterManager.send(alertDto.getUserId(), alertDto.getMessage());
    }
}