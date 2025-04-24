package com.couponmoa.backend.couponmoanotification.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    COUPON_CREATE("coupon-create", "쿠폰 발행 알림", "'%s'에서 새 쿠폰이 발행되었습니다."),
    COUPON_ISSUE("coupon-issue", "쿠폰 발급 알림", "'%s' 쿠폰이 발급되었습니다."),
    COUPON_EXPIRE("coupon-expire", "쿠폰 만료 임박", "'%s' 쿠폰이 %s에 만료됩니다.")
    ;

    private final String eventName;
    private final String title;
    private final String content;
}
