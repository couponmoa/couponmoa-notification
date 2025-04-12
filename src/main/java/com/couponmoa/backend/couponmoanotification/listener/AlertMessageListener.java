package com.couponmoa.backend.couponmoanotification.listener;

import com.couponmoa.backend.couponmoanotification.dto.CouponAlertDto;
import com.couponmoa.backend.couponmoanotification.dto.MessageQueueDto;
import com.couponmoa.backend.couponmoanotification.service.SseEmitterService;
import com.couponmoa.backend.couponmoanotification.service.EmailSenderService;
import com.couponmoa.backend.couponmoanotification.service.WebfluxService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertMessageListener {

    private final EmailSenderService emailSenderService;
    private final SseEmitterService sseEmitterService;
    private final WebfluxService webfluxService;

    @SqsListener("couponmoa-queue")
    public void receiveMQ(@Payload MessageQueueDto message) {
        emailSenderService.sendEmail(message);
    }

    // SseEmitter 기반. userId에 저장된 emitter 찾아서 알림 전송
    @SqsListener("coupon-alert-queue")
    public void receiveCouponMQ(@Payload CouponAlertDto alertDto) {
        sseEmitterService.send(alertDto.getUserId(), alertDto.getMessage());
    }

    // Webflux 기반. userId에 저장된 sink 찾아서 알림 전송(멀티캐스트)
//    @SqsListener("coupon-alert-queue")
//    public void receiveCouponMQ(@Payload CouponAlertDto alertDto) {
//        webfluxService.send(alertDto.getUserId(), alertDto.getMessage());
//    }
}