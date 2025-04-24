package com.couponmoa.backend.couponmoanotification.domain.sse.controller;

import com.couponmoa.backend.couponmoanotification.domain.sse.service.SseEmitterService;
import com.couponmoa.backend.couponmoanotification.domain.sse.service.SseWebfluxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import static com.couponmoa.backend.couponmoanotification.common.consts.RequestHeaderConstants.USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SseController {

    private final SseEmitterService sseEmitterService;
    private final SseWebfluxService sseWebfluxService;

    @GetMapping("/v1/sse/subscribe")
    public SseEmitter subscribeV1(@RequestHeader(USER_ID) Long userId) {
        return sseEmitterService.subscribe(userId);
    }

    @GetMapping(value = "/v2/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> subscribeV2(@RequestHeader(USER_ID) Long userId) {
        return sseWebfluxService.subscribe(userId);
    }
}

