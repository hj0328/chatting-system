package com.chatting.system.core.service;

import com.chatting.system.core.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private static final String ROOM_STREAM_PREFIX = "chat:stream:room:";
    private static final String ROOM_PUBSUB_PREFIX = "chat:pubsub:room:";
    private static final String DIRECT_STREAM_PREFIX = "chat:stream:dm:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void sendToRoom(ChatMessageDto chatMessage) {
        String roomId = chatMessage.getRoomId();

        // Room별 Stream key와 Pub/Sub 채널명
        String streamKey = ROOM_STREAM_PREFIX + roomId;
        String pubsubChannel = ROOM_PUBSUB_PREFIX + roomId;

        // Redis Stream에 메시지 저장
        redisTemplate.opsForStream().add(streamKey, Map.of("payload", chatMessage));

        // Redis Pub/Sub 채널로 브로드캐스트
        redisTemplate.convertAndSend(pubsubChannel, chatMessage);

        log.info("Saved to Stream {} and published to {}. payload: {}", streamKey, pubsubChannel, chatMessage);
    }

    public void sendDirect(ChatMessageDto chatMessage) {
        String receiver = chatMessage.getReceiver();

        String streamKey = DIRECT_STREAM_PREFIX + receiver;
        redisTemplate.opsForStream().add(streamKey, Map.of("payload", chatMessage));
    }
}
