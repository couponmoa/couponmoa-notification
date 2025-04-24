package com.couponmoa.backend.couponmoanotification.domain.sqs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CouponIssueMessage {
    private final Long userId;
    private final Long userCouponId;
    private final String couponName;
    private final LocalDateTime expiryDate;
}