package com.couponmoa.backend.couponmoanotification.domain.sse.subscriber;

import com.couponmoa.backend.couponmoanotification.domain.sse.dto.SseDto;
import com.couponmoa.backend.couponmoanotification.domain.sse.service.SseEmitterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.MessageListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class SseRedisSubscriber implements MessageListener {

    private final SseEmitterService sseEmitterService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            SseDto sseDto = objectMapper.readValue(json, SseDto.class);
            sseEmitterService.send(sseDto); // 인스턴스 내 사용자에게만 전송
        } catch (IOException e) {
            log.error("Redis 구독 메시지 처리 실패", e);
        }
    }
}