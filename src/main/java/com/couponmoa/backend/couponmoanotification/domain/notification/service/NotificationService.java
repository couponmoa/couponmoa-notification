package com.couponmoa.backend.couponmoanotification.domain.notification.service;

import com.couponmoa.backend.couponmoanotification.domain.email.dto.EmailDto;
import com.couponmoa.backend.couponmoanotification.domain.email.service.EmailSenderService;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponCreateMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponExpireMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponIssueMessage;
import com.couponmoa.backend.couponmoanotification.domain.notification.entity.Notification;
import com.couponmoa.backend.couponmoanotification.domain.notification.repository.NotificationRepository;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponUseMessage;
import com.couponmoa.backend.couponmoanotification.domain.sse.dto.SseDto;
import com.couponmoa.backend.couponmoanotification.domain.sse.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;
    private final EmailSenderService emailSenderService;

    public void handleCouponCreateMessage(CouponCreateMessage message) {
        EmailDto emailDto = EmailDto.from(message);
        emailSenderService.send(emailDto);
    }

    @Transactional
    public void handleCouponIssueMessage(CouponIssueMessage message) {
        Notification issueNotification = Notification.forIssue(message.getUserCouponId());
        notificationRepository.save(issueNotification);

        if (shouldCreateExpireNotification(message.getExpiryDate())) { // 쿠폰 만료일이 하루 이상 남은 경우 만료 알림 저장
            Notification expireNotification = Notification.forExpire(message.getUserCouponId());
            notificationRepository.save(expireNotification);
        }

        try {
            SseDto sseDto = SseDto.from(message, issueNotification);
            sseEmitterService.send(sseDto);
            issueNotification.markAsSent();
        } catch (Exception e) {
            issueNotification.markAsFailed();
        }
    }

    @Transactional
    public void handleCouponExpireMessage(CouponExpireMessage message) {
        try {
            EmailDto emailDto = EmailDto.from(message);
            emailSenderService.send(emailDto);
            notificationRepository.markExpireNotificationAsSent(message.getUserCouponIdList());
        } catch (Exception e) {
            notificationRepository.markExpireNotificationAsFailed(message.getUserCouponIdList());
        }
    }

    @Transactional
    public void handleCouponUseMessage(CouponUseMessage message) {
        notificationRepository.deleteExpireNotificationByUserCouponId(message.getUserCouponId());
    }

    private boolean shouldCreateExpireNotification(LocalDateTime expiryDate) {
        return expiryDate.isAfter(LocalDateTime.now().plusDays(1));
    }
}
