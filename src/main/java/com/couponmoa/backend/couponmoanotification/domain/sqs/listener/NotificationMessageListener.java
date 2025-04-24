package com.couponmoa.backend.couponmoanotification.domain.sqs.listener;

import com.couponmoa.backend.couponmoanotification.domain.notification.service.NotificationService;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponCreateMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponExpireMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponIssueMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponUserMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMessageListener {

    private final NotificationService notificationService;

    @SqsListener("coupon-create-queue")
    public void receiveCouponCreateMessage(CouponCreateMessage message) {
        notificationService.handleCouponCreateMessage(message);
    }

    @SqsListener("coupon-issue-v1-queue")
    public void receiveCouponIssueMessageV1(CouponIssueMessage message) {
        notificationService.handleCouponIssueMessageV1(message);
    }

    @SqsListener("coupon-issue-v2-queue")
    public void receiveCouponIssueMessageV2(CouponIssueMessage message) {
        notificationService.handleCouponIssueMessageV2(message);
    }

    @SqsListener("coupon-expire-queue")
    public void receiveCouponExpireMessage(CouponExpireMessage message) {
        notificationService.handleCouponExpireMessage(message);
    }

    @SqsListener("coupon-use-queue")
    public void receiveCouponUseMessage(CouponUserMessage message) {
        notificationService.handleCouponUseMessage(message);
    }
}