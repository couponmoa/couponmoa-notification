package com.couponmoa.backend.couponmoanotification.domain.notification.service;

import com.couponmoa.backend.couponmoanotification.domain.email.dto.EmailDto;
import com.couponmoa.backend.couponmoanotification.domain.email.service.EmailSenderService;
import com.couponmoa.backend.couponmoanotification.domain.notification.entity.Notification;
import com.couponmoa.backend.couponmoanotification.domain.notification.enums.NotificationStatus;
import com.couponmoa.backend.couponmoanotification.domain.notification.repository.NotificationRepository;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponCreateMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponExpireMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponIssueMessage;
import com.couponmoa.backend.couponmoanotification.domain.sqs.dto.CouponUseMessage;
import com.couponmoa.backend.couponmoanotification.domain.sse.dto.SseDto;
import com.couponmoa.backend.couponmoanotification.domain.sse.service.SseEmitterService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private SseEmitterService sseEmitterService;
    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @InjectMocks
    private NotificationService notificationService;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class HandleCouponCreateMessageTests {

        private static final CouponCreateMessage message = new CouponCreateMessage("storeName", List.of("test@test.com"));

        @Test
        @Order(1)
        void 쿠폰_생성_알림_이메일_전송_실패() {
            doThrow(new IllegalStateException()).when(emailSenderService)
                    .send(any(EmailDto.class));

            assertThrows(IllegalStateException.class, () -> notificationService.handleCouponCreateMessage(message));
        }

        @Test
        @Order(2)
        void 쿠폰_생성_알림_이메일_전송_성공() {
            notificationService.handleCouponCreateMessage(message);

            verify(emailSenderService, times(1)).send(any(EmailDto.class));
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class HandleCouponIssueMessageTests {

        private static final CouponIssueMessage message = new CouponIssueMessage(1L, 1L, "couponName", LocalDateTime.now().plusMonths(1));

        @Test
        @Order(1)
        void 쿠폰_발급_알림_쿠폰_발급_알림만_저장_성공() {
            CouponIssueMessage message = new CouponIssueMessage(1L, 1L, "couponName", LocalDateTime.now());
            when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(), any())).thenReturn(1L);
            notificationService.handleCouponIssueMessage(message);

            verify(notificationRepository, times(1)).save(any(Notification.class));
        }

        @Test
        @Order(2)
        void 쿠폰_발급_알림_쿠폰_만료_알림도_저장_성공() {
            when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(), any())).thenReturn(1L);
            notificationService.handleCouponIssueMessage(message);

            verify(notificationRepository, times(2)).save(any(Notification.class));
        }

        @Test
        @Order(3)
        void 쿠폰_발급_알림_sse_전송_실패() {
            when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(), any())).thenReturn(1L);
            doThrow(new RuntimeException()).when(sseEmitterService).send(any(SseDto.class));

            notificationService.handleCouponIssueMessage(message);

            verify(notificationRepository, atLeastOnce()).save(notificationCaptor.capture());
            Notification issueNotification = notificationCaptor.getAllValues().get(0);
            assertEquals(NotificationStatus.FAILED, issueNotification.getStatus());
        }

        @Test
        @Order(4)
        void 쿠폰_발급_알림_sse_전송_성공() {
            when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(), any())).thenReturn(1L);
            notificationService.handleCouponIssueMessage(message);

            verify(notificationRepository, atLeastOnce()).save(notificationCaptor.capture());
            Notification issueNotification = notificationCaptor.getAllValues().get(0);
            assertEquals(NotificationStatus.SENT, issueNotification.getStatus());
        }

        @Test
        @Order(5)
        void 쿠폰_발급_알림_멱등성_체크_락_획득_실패_시_알림_실패() {
            when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(), any())).thenReturn(0L);

            notificationService.handleCouponIssueMessage(message);

            verify(notificationRepository, never()).save(any());
            verify(sseEmitterService, never()).send(any());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class HandleCouponExpireMessageTests {

        private static final CouponExpireMessage message = new CouponExpireMessage("couponName", LocalDateTime.now(), List.of("test@test.com"), List.of(1L));

        @Test
        @Order(1)
        void 쿠폰_만료_알림_이메일_전송_실패() {
            when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(), any())).thenReturn(1L);

            doThrow(new IllegalStateException()).when(emailSenderService).send(any(EmailDto.class));

            notificationService.handleCouponExpireMessage(message);

            verify(notificationRepository, times(1)).markExpireNotificationAsFailed(anyList());
            verify(notificationRepository, never()).markExpireNotificationAsSent(anyList());
        }

        @Test
        @Order(2)
        void 쿠폰_만료_알림_이메일_전송_성공() {
            when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(), any())).thenReturn(1L);

            notificationService.handleCouponExpireMessage(message);

            verify(notificationRepository, never()).markExpireNotificationAsFailed(anyList());
            verify(notificationRepository, times(1)).markExpireNotificationAsSent(anyList());
        }

        @Test
        @Order(3)
        void 쿠폰_만료_알림_멱등성_체크_락_획득_실패_시_알림_실패() {
            when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(), any())).thenReturn(0L);

            notificationService.handleCouponExpireMessage(message);

            verify(notificationRepository, never()).markExpireNotificationAsFailed(anyList());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class HandleCouponUseMessageTests {

        private static final CouponUseMessage message = new CouponUseMessage(1L);

        @Test
        @Order(1)
        void 쿠폰_사용_메시지_수신_시_만료_알림_제거() {
            notificationService.handleCouponUseMessage(message);

            verify(notificationRepository, times(1)).deleteExpireNotificationByUserCouponId(anyLong());
        }
    }
}