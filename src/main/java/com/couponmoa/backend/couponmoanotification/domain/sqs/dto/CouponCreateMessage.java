package com.couponmoa.backend.couponmoanotification.domain.sqs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CouponCreateMessage {
    private final String storeName;
    private final List<String> emailList;
}
