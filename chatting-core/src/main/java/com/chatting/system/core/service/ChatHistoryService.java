package com.chatting.system.core.service;

import com.chatting.system.core.dto.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String ROOM_STREAM_PREFIX = "chat:stream:room:";
    private static final String DIRECT_STREAM_PREFIX = "chat:stream:dm:";
    private static final int MAX_UNREAD_COUNT = 50; // 최대 50개의 읽지 않은 메세지 조회

    public List<ChatMessageDto> readRecentRoomMessages(String roomId) {
        String streamKey = ROOM_STREAM_PREFIX + roomId;

        // 가장 마지막 50개 메세지 조회
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                .reverseRange(streamKey, Range.unbounded(), Limit.limit().count(MAX_UNREAD_COUNT));

        Collections.reverse(records);

        List<ChatMessageDto> result = new ArrayList<>();
        for (MapRecord<String, Object, Object> record : records) {
            Object raw = record.getValue().get("payload");
            ChatMessageDto dto = objectMapper.convertValue(raw, ChatMessageDto.class);
            result.add(dto);
        }

        return result;
    }

    public List<ChatMessageDto> readRecentDirectMessages(String roomId) {
        String streamKey = DIRECT_STREAM_PREFIX + roomId;
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                .reverseRange(streamKey, Range.unbounded(), Limit.limit().count(MAX_UNREAD_COUNT));

        Collections.reverse(records);

        List<ChatMessageDto> result = new ArrayList<>();
        for (MapRecord<String, Object, Object> record : records) {
            Object raw = record.getValue().get("payload");
            ChatMessageDto dto = objectMapper.convertValue(raw, ChatMessageDto.class);
            result.add(dto);
        }

        return result;
    }
}
