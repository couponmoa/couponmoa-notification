package com.couponmoa.backend.couponmoanotification.controller;

import com.couponmoa.backend.couponmoanotification.infra.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class SseController {

    private final SseEmitterManager sseEmitterManager;

    // SseEmitter 방식
    // 클라이언트와 sse 연결 (클라이언트 측에서 실행하는 api)
    @GetMapping("/{userId}/notifications/emitter")
    public SseEmitter subscribe(@PathVariable Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃
        sseEmitterManager.add(userId, emitter);
        return emitter;
    }


}

