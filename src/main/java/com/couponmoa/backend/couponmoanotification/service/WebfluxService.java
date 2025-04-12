package com.couponmoa.backend.couponmoanotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WebfluxService {

    private final Map<Long, Sinks.Many<ServerSentEvent<String>>> sinkMap = new ConcurrentHashMap<>();
    private final WebClient webClient;

    // 클라이언트 요청시 sinks.many 생성 및 flux 반환
    public Flux<ServerSentEvent<String>> subscribe(Long userId) {
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();
        sinkMap.put(userId, sink);
        return sink.asFlux().doFinally(signalType -> sinkMap.remove(userId));
    }

    // 알림 전송
    public void send(Long userId, String message, Long notificationId) {
        Sinks.Many<ServerSentEvent<String>> sink = sinkMap.get(userId); // 구독한 sink 가져옴
        if (sink != null) {
            // sse 전송
            sink.tryEmitNext(ServerSentEvent.builder(message).build());
            webClient.post() // notification 상태 변경
                    .uri("http://couponmoa-api/notifications/{id}/notified",notificationId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribe();
        }
    }
}