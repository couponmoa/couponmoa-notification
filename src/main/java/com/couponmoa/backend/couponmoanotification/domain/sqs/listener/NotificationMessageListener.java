package com.couponmoa.backend.couponmoanotification.domain.sqs.listener;

import com.couponmoa.backend.couponmoanotification.domain.notification.service.NotificationService;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponCreateMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponExpireMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponIssueMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponUseMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMessageListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @SqsListener("coupon-create-queue")
    public void receiveCouponCreateMessage(@Payload String rawMessage) {
        try {
            CouponCreateMessage message = objectMapper.readValue(rawMessage, CouponCreateMessage.class);
            notificationService.handleCouponCreateMessage(message);
        } catch (Exception e) {
            log.error("coupon-create-queue: {}", e.getMessage(), e);
        }
    }

    @SqsListener("coupon-issue-v1-queue")
    public void receiveCouponIssueMessage(@Payload String rawMessage) {
        try {
            CouponIssueMessage message = objectMapper.readValue(rawMessage, CouponIssueMessage.class);
            notificationService.handleCouponIssueMessage(message);
        } catch (Exception e) {
            log.error("coupon-issue-v1-queue: {}", e.getMessage(), e);
        }
    }

    @SqsListener("coupon-expire-queue")
    public void receiveCouponExpireMessage(@Payload String rawMessage) {
        try {
            CouponExpireMessage message = objectMapper.readValue(rawMessage, CouponExpireMessage.class);
            notificationService.handleCouponExpireMessage(message);
        } catch (Exception e) {
            log.error("coupon-expire-queue: {}", e.getMessage(), e);
        }
    }

    @SqsListener("coupon-use-queue")
    public void receiveCouponUseMessage(@Payload String rawMessage) {
        try {
            CouponUseMessage message = objectMapper.readValue(rawMessage, CouponUseMessage.class);
            notificationService.handleCouponUseMessage(message);
        } catch (Exception e) {
            log.error("coupon-use-queue: {}", e.getMessage(), e);
        }
    }
}