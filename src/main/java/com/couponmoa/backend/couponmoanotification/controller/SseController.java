package com.couponmoa.backend.couponmoanotification.controller;

import com.couponmoa.backend.couponmoanotification.infra.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {

    private final SseEmitterManager sseEmitterManager;

    // 클라이언트와 sse 연결
    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃
        sseEmitterManager.add(userId, emitter);
        return emitter;
    }
}

