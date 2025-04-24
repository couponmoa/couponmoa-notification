package com.couponmoa.backend.couponmoanotification.service;

import com.couponmoa.backend.couponmoanotification.domain.sse.service.SseEmitterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SseEmitterServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec uriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

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
        Long userId = 1L;
        String message = "message";
        Long notificationId = 1L;
        SseEmitter emitter = mock(SseEmitter.class);
        doThrow(new IOException("forced")).when(emitter).send(message);

        Field emittersField = SseEmitterService.class.getDeclaredField("emitters");
        emittersField.setAccessible(true);
        Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
        emitters.put(userId, emitter);

        emittersField.set(sseEmitterService, emitters);

        sseEmitterService.send(userId, message, notificationId);

        verify(emitter).completeWithError(any(IOException.class));
    }

    @Test
    void 알림_전송_실패_emitter_null() throws NoSuchFieldException, IOException, IllegalAccessException {
        Long userId = 1L;
        String message = "message";
        Long notificationId = 1L;
        SseEmitter emitter = mock(SseEmitter.class);

        Field emittersField = SseEmitterService.class.getDeclaredField("emitters");
        emittersField.setAccessible(true);
        Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

        emittersField.set(sseEmitterService, emitters);

        sseEmitterService.send(userId, message, notificationId);

        verify(emitter,never()).send(message);
    }

    @Test
    void 알림_전송_성공() throws IOException, NoSuchFieldException, IllegalAccessException {
        Long userId = 1L;
        String message = "message";
        Long notificationId = 1L;
        SseEmitter sseEmitter = mock(SseEmitter.class);

        Field emittersField = SseEmitterService.class.getDeclaredField("emitters");
        emittersField.setAccessible(true);
        Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
        emitters.put(userId, sseEmitter);

        given(webClient.post()).willReturn(uriSpec);
        given(uriSpec.uri(anyString(), eq(notificationId))).willReturn(uriSpec);
        given(uriSpec.header(anyString(), anyString())).willReturn(uriSpec);
        given(uriSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(Void.class)).willReturn(Mono.empty());

        emittersField.set(sseEmitterService, emitters);

        sseEmitterService.send(userId, message, notificationId);
        verify(sseEmitter).send(message);
        verify(webClient).post();
    }

}
