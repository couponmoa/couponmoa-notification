package com.couponmoa.backend.couponmoanotification.domain.sse.dto;

import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponIssueMessage;
import com.couponmoa.backend.couponmoanotification.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SseDto {
    private final Long userId;
    private final Long notificationId;
    private final String eventName;
    private final String content;

    public static SseDto from(CouponIssueMessage message, Notification notification) {
        String formattedContent = String.format(notification.getType().getContent(), message.getCouponName());
        return SseDto.builder()
                .userId(message.getUserId())
                .notificationId(notification.getId())
                .eventName(notification.getType().getEventName())
                .content(formattedContent)
                .build();
    }
}
