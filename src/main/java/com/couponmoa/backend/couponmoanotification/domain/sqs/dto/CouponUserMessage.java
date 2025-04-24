package com.couponmoa.backend.couponmoanotification.domain.sqs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponUserMessage {
    private final Long userCouponId;
}
