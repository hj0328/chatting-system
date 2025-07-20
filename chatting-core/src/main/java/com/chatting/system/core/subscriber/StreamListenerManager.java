package com.chatting.system.core.subscriber;

import com.chatting.system.core.dto.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamListenerManager {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, Future<?>> listeners = new ConcurrentHashMap<>();

    public void startListening(String userId) {
        if (listeners.containsKey(userId)) return;

        Future<?> future = executor.submit(() -> listenLoop(userId));
        listeners.put(userId, future);
    }

    public void stopListening(String userId) {
        Future<?> task = listeners.remove(userId);
        if (task != null) {
            task.cancel(true);
        }
    }

    private void listenLoop(String userId) {
        String streamKey = "chat:stream:dm:" + userId;

        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 블로킹 읽기
                List<MapRecord<String, Object, Object>> records =
                        redisTemplate.opsForStream().read(
                                StreamReadOptions.empty().block(Duration.ofSeconds(30)),
                                StreamOffset.latest(streamKey)
                        );

                if (records != null) {
                    for (MapRecord<String, Object, Object> record : records) {
                        String rawPayload = (String) record.getValue().get("payload");
                        ChatMessageDto message = objectMapper.readValue(rawPayload, ChatMessageDto.class);

                        // WebSocket 전송
                        messagingTemplate.convertAndSend(
                                "/user/" + userId + "/queue/messages", message
                        );
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // 다시 인터럽트 상태 복구
            }
            log.warn("유저 {} Stream listener 종료됨: {}", userId, e.getStackTrace()[0]);
        }
    }
}
