package com.couponmoa.backend.couponmoanotification.controller;

import com.couponmoa.backend.couponmoanotification.service.SseEmitterService;
import com.couponmoa.backend.couponmoanotification.service.WebfluxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SseController.class)
public class SseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SseEmitterService sseEmitterService;

    @MockitoBean
    private WebfluxService webfluxService;

    @Test
    void emitter_subscribe() throws Exception {
        Long userId = 1L;
        SseEmitter mockSseEmitter = Mockito.mock(SseEmitter.class);
        given(sseEmitterService.subscribe(anyLong())).willReturn(mockSseEmitter);

        mockMvc.perform(get("/api/v1/users/{userId}/notifications/emitter",userId))
                .andExpect(status().isOk());
    }

    @Test
    void flux_subscribe() throws Exception {
        Long userId = 1L;
        Flux<ServerSentEvent<String>> mockFlux = Flux.just(
                ServerSentEvent.builder("Message 1").build(),
                ServerSentEvent.builder("Message 2").build()
        );
        given(webfluxService.subscribe(anyLong())).willReturn(mockFlux);

        mockMvc.perform(get("/api/v1/users/{userId}/notifications/webflux",userId))
                .andExpect(status().isOk());
    }
}
