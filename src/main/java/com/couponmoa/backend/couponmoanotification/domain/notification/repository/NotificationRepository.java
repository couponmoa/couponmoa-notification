package com.couponmoa.backend.couponmoanotification.domain.notification.repository;

import com.couponmoa.backend.couponmoanotification.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'SENT' " +
            "WHERE n.userCouponId IN :userCouponIdList AND n.type = 'COUPON_EXPIRE'")
    void markExpireNotificationAsSent(List<Long> userCouponIdList);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'FAILED' " +
            "WHERE n.userCouponId IN :userCouponIdList AND n.type = 'COUPON_EXPIRE'")
    void markExpireNotificationAsFailed(List<Long> userCouponIdList);
}
