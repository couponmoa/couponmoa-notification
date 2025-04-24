package com.couponmoa.backend.couponmoanotification.domain.sse.service;

import com.couponmoa.backend.couponmoanotification.domain.sse.dto.SseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SseEmitterServiceTest {

    @InjectMocks
    private SseEmitterService sseEmitterService;

    @Test
    void 구독_성공() {
        Long userId = 1L;
        SseEmitter result = sseEmitterService.subscribe(userId);
        assertNotNull(result);
    }

    @Test
    void 알림_전송_실패() throws NoSuchFieldException, IOException, IllegalAccessException {
        SseDto sseDto = new SseDto(1L, 1L, "eventName", "content");
        SseEmitter emitter = mock(SseEmitter.class);
        doThrow(new IOException("forced")).when(emitter).send(any(SseEmitter.SseEventBuilder.class));

        Field emittersField = SseEmitterService.class.getDeclaredField("emitters");
        emittersField.setAccessible(true);
        Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
        emitters.put(sseDto.getUserId(), emitter);

        emittersField.set(sseEmitterService, emitters);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> sseEmitterService.send(sseDto));

        assertThat(thrown.getCause()).isInstanceOf(IOException.class);
        verify(emitter).completeWithError(any(IOException.class));
    }

    @Test
    void 알림_전송_실패_emitter_null() throws NoSuchFieldException, IOException, IllegalAccessException {
        SseDto sseDto = new SseDto(1L, 1L, "eventName", "content");

        Field emittersField = SseEmitterService.class.getDeclaredField("emitters");
        emittersField.setAccessible(true);
        Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

        emittersField.set(sseEmitterService, emitters);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> sseEmitterService.send(sseDto));

        assertEquals("사용자 SSE 연결이 존재하지 않습니다: userId=" + sseDto.getUserId(), thrown.getMessage());
    }

    @Test
    void 알림_전송_성공() throws IOException, NoSuchFieldException, IllegalAccessException {
        SseDto sseDto = new SseDto(1L, 1L, "eventName", "content");
        SseEmitter sseEmitter = mock(SseEmitter.class);

        Field emittersField = SseEmitterService.class.getDeclaredField("emitters");
        emittersField.setAccessible(true);
        Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
        emitters.put(sseDto.getUserId(), sseEmitter);

        emittersField.set(sseEmitterService, emitters);

        sseEmitterService.send(sseDto);

        verify(sseEmitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }

}
