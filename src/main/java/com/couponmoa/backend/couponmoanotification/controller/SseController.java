package com.couponmoa.backend.couponmoanotification.controller;

import com.couponmoa.backend.couponmoanotification.service.SseEmitterService;
import com.couponmoa.backend.couponmoanotification.service.WebfluxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class SseController {

    private final SseEmitterService sseEmitterService;
    private final WebfluxService webfluxService;

    // SseEmitter 방식
    // 클라이언트와 sse 연결 (클라이언트 측에서 실행하는 api)
    @CrossOrigin(origins = "*")
    @GetMapping("/{userId}/notifications/emitter")
    public SseEmitter subscribe(@PathVariable Long userId) {
        return sseEmitterService.subscribe(userId);
    }

    // webflux 방식
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/{userId}/notifications/webflux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> webfluxSubscribe(@PathVariable Long userId) {
        return webfluxService.subscribe(userId);
    }

    // 성능 테스트용(SQS 없이 바로 알림 전송)
    @PostMapping("/test-notify-emitter")
    public ResponseEntity<Void> testNotifyEmitter(@RequestParam Long userId, @RequestParam String message) {
        sseEmitterService.send(userId, message, null); // WebFlux 또는 SseEmitter 방식 호출
        return ResponseEntity.ok().build();
    }
    // 성능 테스트용(SQS 없이 바로 알림 전송)
    @PostMapping("/test-notify-webflux")
    public ResponseEntity<Void> testNotifyWebflux(@RequestParam Long userId, @RequestParam String message) {
        webfluxService.send(userId, message, null); // WebFlux 또는 SseEmitter 방식 호출
        return ResponseEntity.ok().build();
    }


}

