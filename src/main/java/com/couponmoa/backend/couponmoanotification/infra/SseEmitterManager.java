package com.couponmoa.backend.couponmoanotification.infra;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterManager {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 클라이언트에서 연결 요청할 때 호출됨. sseEmitter 생성 및 저장
    public void add(Long userId, SseEmitter emitter) {
        this.emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
    }

    // userId에 저장된 emitter 찾아서 알림 전송
    public void send(Long userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("coupon-alert").data(message));
            } catch (IOException e) {
                emitters.remove(userId);
                emitter.completeWithError(e);
            }
        }
    }
}
