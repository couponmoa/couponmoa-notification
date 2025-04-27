package com.couponmoa.backend.couponmoanotification.domain.email.dto;

import com.couponmoa.backend.couponmoanotification.common.util.DateTimeFormatUtil;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponCreateMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponExpireMessage;
import com.couponmoa.backend.couponmoanotification.domain.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EmailDto {
    private final List<String> emailList;
    private final String subject;
    private final String content;

    public static EmailDto from(CouponExpireMessage message) {
        NotificationType type = NotificationType.COUPON_EXPIRE;
        String formattedDate = DateTimeFormatUtil.format(message.getExpiryDate());
        String formattedContent = String.format(type.getContent(), message.getCouponName(), formattedDate);
        return new EmailDto(message.getEmailList(), type.getTitle(), formattedContent);
    }

    public static EmailDto from(CouponCreateMessage message) {
        NotificationType type = NotificationType.COUPON_CREATE;
        String formattedContent = String.format(type.getContent(), message.getStoreName());
        return new EmailDto(message.getEmailList(), type.getTitle(), formattedContent);
    }
}
