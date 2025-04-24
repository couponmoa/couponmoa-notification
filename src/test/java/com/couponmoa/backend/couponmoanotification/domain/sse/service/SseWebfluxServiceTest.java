package com.couponmoa.backend.couponmoanotification.domain.sse.service;

import com.couponmoa.backend.couponmoanotification.domain.sse.dto.SseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SseWebfluxServiceTest {

    @InjectMocks
    private SseWebfluxService sseWebfluxService;

    @Test
    void 구독_성공() {
        Long userId = 1L;
        Flux<ServerSentEvent<String>> result = sseWebfluxService.subscribe(userId);
        assertNotNull(result);
    }

    @Test
    void 알림_전송_실패_구독_안됨() throws NoSuchFieldException, IllegalAccessException {
        SseDto sseDto = new SseDto(1L, 1L, "eventName", "content");

        Field sinkField = SseWebfluxService.class.getDeclaredField("sinks");
        sinkField.setAccessible(true);
        Map<Long, Sinks.Many<ServerSentEvent<String>>> sinks = new ConcurrentHashMap<>();

        sinkField.set(sseWebfluxService, sinks);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> sseWebfluxService.send(sseDto));

        assertEquals("사용자 SSE 연결이 존재하지 않습니다: userId=" + sseDto.getUserId(), thrown.getMessage());
    }

    @Test
    void 알림_전송_성공() throws NoSuchFieldException, IllegalAccessException {
        SseDto sseDto = new SseDto(1L, 1L, "eventName", "content");

        Sinks.Many<ServerSentEvent<String>> sink = mock(Sinks.Many.class);

        Field sinkField = SseWebfluxService.class.getDeclaredField("sinks");
        sinkField.setAccessible(true);
        Map<Long, Sinks.Many<ServerSentEvent<String>>> sinks = new ConcurrentHashMap<>();
        sinks.put(sseDto.getUserId(), sink);

        sinkField.set(sseWebfluxService, sinks);

        sseWebfluxService.send(sseDto);

        verify(sink, times(1)).tryEmitNext(any(ServerSentEvent.class));
    }
}
