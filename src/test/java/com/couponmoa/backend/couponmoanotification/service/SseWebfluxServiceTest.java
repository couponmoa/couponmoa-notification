package com.couponmoa.backend.couponmoanotification.service;

import com.couponmoa.backend.couponmoanotification.domain.sse.service.SseWebfluxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SseWebfluxServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec uriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SseWebfluxService sseWebfluxService;

    @Test
    void 구독_성공() {
        Long userId = 1L;
        Flux<ServerSentEvent<String>> result = sseWebfluxService.subscribe(userId);
        assertNotNull(result);
    }

    @Test
    void 알림_전송_실패_구독_안됨() throws NoSuchFieldException {
        Long userId = 1L;
        String message = "message";
        Long notificationId = 1L;

        Field sinkField = SseWebfluxService.class.getDeclaredField("sinkMap");
        sinkField.setAccessible(true);
        Map<Long, Sinks.Many<ServerSentEvent<String>>> sinkMap = new ConcurrentHashMap<>();

        sseWebfluxService.send(userId, message, notificationId);

        verify(webClient, never()).post();
    }

    @Test
    void 알림_전송_성공() throws NoSuchFieldException, IllegalAccessException {
        Long userId = 1L;
        String message = "message";
        Long notificationId = 1L;

        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();

        Field sinkField = SseWebfluxService.class.getDeclaredField("sinkMap");
        sinkField.setAccessible(true);
        Map<Long, Sinks.Many<ServerSentEvent<String>>> sinkMap = new ConcurrentHashMap<>();
        sinkMap.put(userId, sink);

        given(webClient.post()).willReturn(uriSpec);
        given(uriSpec.uri(anyString(), eq(notificationId))).willReturn(uriSpec);
        given(uriSpec.header(anyString(), anyString())).willReturn(uriSpec);
        given(uriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(Void.class)).willReturn(Mono.empty());

        sinkField.set(sseWebfluxService, sinkMap);

        sseWebfluxService.send(userId, message, notificationId);

        verify(webClient).post();
    }
}
