package com.couponmoa.backend.couponmoanotification.domain.sse.service;

import com.couponmoa.backend.couponmoanotification.domain.sse.dto.SseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseWebfluxService {
    private static final String NOTIFICATION = "notification";

    private final Map<Long, Sinks.Many<ServerSentEvent<String>>> sinks = new ConcurrentHashMap<>();

    public Flux<ServerSentEvent<String>> subscribe(Long userId) {
        if (sinks.containsKey(userId)) {
            sinks.get(userId).tryEmitComplete();
        }

        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();
        sinks.put(userId, sink);
        log.info("SSE 연결 시작: userId={}", userId);

        return sink.asFlux()
                .doFinally(signalType -> {
                    sinks.remove(userId);
                    log.info("SSE 연결 해제: userId={}", userId);
                });
    }

    public void send(SseDto dto) {
        Sinks.Many<ServerSentEvent<String>> sink = sinks.get(dto.getUserId());

        if (sink == null) {
            throw new IllegalStateException("사용자 SSE 연결이 존재하지 않습니다: userId=" + dto.getUserId());
        }

        sink.tryEmitNext(ServerSentEvent.builder(dto.getContent())
                .id(NOTIFICATION + dto.getNotificationId())
                .event(dto.getEventName())
                .build());

        log.info("SSE 알림 전송 완료: userId={}, notificationId={}", dto.getUserId(), dto.getNotificationId());
    }
}
