package com.couponmoa.backend.couponmoanotification.domain.sqs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CouponExpireMessage {
    private final String couponName;
    private final LocalDateTime expiryDate;
    private final List<String> emailList;
    private final List<Long> userCouponIdList;
}
