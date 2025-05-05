package com.couponmoa.backend.couponmoanotification.domain.notification.service;

import com.couponmoa.backend.couponmoanotification.domain.email.dto.EmailDto;
import com.couponmoa.backend.couponmoanotification.domain.email.service.EmailSenderService;
import com.couponmoa.backend.couponmoanotification.domain.notification.entity.Notification;
import com.couponmoa.backend.couponmoanotification.domain.notification.repository.NotificationRepository;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponCreateMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponExpireMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponIssueMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponUseMessage;
import com.couponmoa.backend.couponmoanotification.domain.sse.dto.SseDto;
import com.couponmoa.backend.couponmoanotification.domain.sse.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailSenderService emailSenderService;
    private final RedisTemplate<String, String> redisTemplate; // 멱등성용
    private final RedisTemplate<String, SseDto> redisTemplateSse; // pub/sub용

    private static final Duration TTL = Duration.ofHours(6);

    @Transactional
    public void handleCouponIssueMessage(CouponIssueMessage message) {
        String key = "coupon-issue:" + message.getCouponName() + ":" + message.getUserCouponId();
        if (!acquireNotificationLock(key)) return;

        Notification issueNotification = Notification.forIssue(message.getUserCouponId());
        notificationRepository.save(issueNotification);

        if (shouldCreateExpireNotification(message.getExpiryDate())) { // 쿠폰 만료일이 하루 이상 남은 경우 만료 알림 저장
            Notification expireNotification = Notification.forExpire(message.getUserCouponId());
            notificationRepository.save(expireNotification);
        }

        try {
            SseDto sseDto = SseDto.from(message, issueNotification);
            redisTemplateSse.convertAndSend("sse-channel", sseDto); // redis pub (모든 인스턴스에 발행)
            issueNotification.markAsSent();
        } catch (Exception e) {
            issueNotification.markAsFailed();
        }
    }

    @Transactional
    public void handleCouponExpireMessage(CouponExpireMessage message) {
        // 멱등성 보장
        String key = "expire-coupon:" + message.getCouponName() + ":" + message.getExpiryDate().toLocalDate();
        if (!acquireNotificationLock(key)) return;

        try {
            EmailDto emailDto = EmailDto.from(message);
            emailSenderService.send(emailDto);
            notificationRepository.markExpireNotificationAsSent(message.getUserCouponIdList());
        } catch (Exception e) {
            notificationRepository.markExpireNotificationAsFailed(message.getUserCouponIdList());
        }
    }

    public void handleCouponCreateMessage(CouponCreateMessage message) {
        EmailDto emailDto = EmailDto.from(message);
        emailSenderService.send(emailDto);
    }

    @Transactional
    public void handleCouponUseMessage(CouponUseMessage message) {
        notificationRepository.deleteExpireNotificationByUserCouponId(message.getUserCouponId());
    }

    private boolean shouldCreateExpireNotification(LocalDateTime expiryDate) {
        return expiryDate.isAfter(LocalDateTime.now().plusDays(1));
    }

    private boolean acquireNotificationLock(String key) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "sent", TTL);
        return Boolean.TRUE.equals(result);
    }
}
