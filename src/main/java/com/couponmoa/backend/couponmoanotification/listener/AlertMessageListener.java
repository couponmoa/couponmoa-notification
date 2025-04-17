package com.couponmoa.backend.couponmoanotification.listener;

import com.couponmoa.backend.couponmoanotification.dto.CouponAlertDto;
import com.couponmoa.backend.couponmoanotification.dto.MessageQueueDto;
import com.couponmoa.backend.couponmoanotification.service.SseEmitterService;
import com.couponmoa.backend.couponmoanotification.service.EmailSenderService;
import com.couponmoa.backend.couponmoanotification.service.WebfluxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @SqsListener("couponmoa-queue")
    public void receiveMQ(@Payload String messageJson) throws JsonProcessingException {
        MessageQueueDto messageDto = objectMapper.readValue(messageJson, MessageQueueDto.class);
        emailSenderService.sendEmail(messageDto);
    }

    // SseEmitter 기반. userId에 저장된 emitter 찾아서 알림 전송
    @SqsListener("coupon-alert-queue")
    public void receiveCouponMQ(@Payload String messageJson) throws JsonProcessingException {
        CouponAlertDto alertDto = objectMapper.readValue(messageJson, CouponAlertDto.class);
        sseEmitterService.send(alertDto.getUserId(), alertDto.getMessage(), alertDto.getNotificationId());
    }

//     Webflux 기반. userId에 저장된 sink 찾아서 알림 전송(멀티캐스트)
//    @SqsListener("coupon-alert-queue")
//    public void receiveCouponMQ(@Payload String messageJson) throws JsonProcessingException {
//        CouponAlertDto alertDto = objectMapper.readValue(messageJson, CouponAlertDto.class);
//        webfluxService.send(alertDto.getUserId(), alertDto.getMessage(), alertDto.getNotificationId());
//    }
}