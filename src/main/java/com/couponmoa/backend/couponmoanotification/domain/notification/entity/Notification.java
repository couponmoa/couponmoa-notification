package com.couponmoa.backend.couponmoanotification.domain.notification.entity;

import com.couponmoa.backend.couponmoanotification.common.entity.BaseEntity;
import com.couponmoa.backend.couponmoanotification.domain.notification.enums.NotificationStatus;
import com.couponmoa.backend.couponmoanotification.domain.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "notifications",
    uniqueConstraints = @UniqueConstraint(columnNames = {"userCouponId", "type"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userCouponId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    private Notification(Long userCouponId, NotificationType type) {
        this.userCouponId = userCouponId;
        this.type = type;
    }

    public static Notification forIssue(Long userCouponId) {
        return new Notification(userCouponId, NotificationType.COUPON_ISSUE);
    }

    public static Notification forExpire(Long userCouponId) {
        return new Notification(userCouponId, NotificationType.COUPON_EXPIRE);
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
    }

    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
    }

    public void markAsUnconfirmed() {
        this.status = NotificationStatus.UNCONFIRMED;
    }
}
