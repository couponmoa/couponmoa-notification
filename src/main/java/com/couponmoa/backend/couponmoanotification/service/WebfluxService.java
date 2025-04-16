package com.couponmoa.backend.couponmoanotification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebfluxService {

    private final Map<Long, Sinks.Many<ServerSentEvent<String>>> sinkMap = new ConcurrentHashMap<>();
    private final WebClient webClient;

    // 클라이언트 요청시 sinks.many 생성 및 flux 반환
    public Flux<ServerSentEvent<String>> subscribe(Long userId) {
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();
        sinkMap.put(userId, sink);
        log.info("SSE 연결 시작: userId={}", userId);
        return sink.asFlux().doFinally(signalType -> sinkMap.remove(userId));
    }

    // 알림 전송
    public void send(Long userId, String message, Long notificationId) {
        Sinks.Many<ServerSentEvent<String>> sink = sinkMap.get(userId); // 구독한 sink 가져옴
        if (sink != null) {
            // sse 전송
            sink.tryEmitNext(ServerSentEvent.builder(message).build());
            log.info("webflux 실행완료");
            webClient.post() // notification 상태 변경
                    .uri("http://localhost:8080/api/v1/notifications/{id}/notified", notificationId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribe();
        } else{
            log.warn("구독 안됨");
        }
    }
}