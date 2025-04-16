package com.couponmoa.backend.couponmoanotification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class SseEmitterService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final WebClient webClient;

    // 클라이언트에서 연결 요청할 때 호출됨. sseEmitter 생성 및 저장
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃
        this.emitters.put(userId, emitter);
        log.info("SSE 연결 시작: userId={}", userId);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        return emitter;
    }

    // TODO: api 서버명 정해지면 그거에 맞게 url 수정 필요
    // userId에 저장된 emitter 찾아서 알림 전송
    public void send(Long userId, String message, Long notificationId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(message);
                log.info("emitter 실행완료");
                webClient.post() // notification 상태 변경
                        .uri("http://localhost:8080/api/v1/notifications/{id}/notified", notificationId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .subscribe();
            } catch (IOException e) {
                emitters.remove(userId);
                emitter.completeWithError(e);
            }
        }
    }
}
