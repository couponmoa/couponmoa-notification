package com.couponmoa.backend.couponmoanotification.domain.sse.service;

import com.couponmoa.backend.couponmoanotification.domain.sse.dto.SseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseEmitterService {

    private static final String NOTIFICATION = "notification";
    private static final Long TIMEOUT = 30 * 60 * 1000L; // 30분

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        if (emitters.containsKey(userId)) {
            emitters.get(userId).complete();
        }

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.put(userId, emitter);
        log.info("SSE 연결 시작: userId={}", userId);

        emitter.onTimeout(emitter::complete);
        emitter.onError(throwable -> emitter.complete());
        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.info("SSE 연결 해제: userId={}", userId);
        });

        return emitter;
    }

    public void send(SseDto dto) {
        SseEmitter emitter = emitters.get(dto.getUserId());

        if (emitter == null) {
            throw new IllegalStateException("사용자 SSE 연결이 존재하지 않습니다: userId=" + dto.getUserId());
        }

        try {
            emitter.send(SseEmitter.event()
                    .id(NOTIFICATION + dto.getNotificationId())
                    .name(dto.getEventName())
                    .data(dto.getContent()));
        } catch (IOException e) {
            emitter.completeWithError(e);
            throw new RuntimeException(e);
        }

        log.info("SSE 알림 전송 완료: userId={}, notificationId={}", dto.getUserId(), dto.getNotificationId());
    }
}
