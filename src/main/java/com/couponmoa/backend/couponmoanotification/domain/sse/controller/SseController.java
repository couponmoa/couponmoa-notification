package com.couponmoa.backend.couponmoanotification.domain.sse.controller;

import com.couponmoa.backend.couponmoanotification.domain.sse.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.couponmoa.backend.couponmoanotification.common.consts.RequestHeaderConstants.USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SseController {

    private final SseEmitterService sseEmitterService;

    @GetMapping("/sse/subscribe")
    public SseEmitter subscribeV1(@RequestHeader(USER_ID) Long userId) {
        return sseEmitterService.subscribe(userId);
    }
}

