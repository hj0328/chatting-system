package com.chatting.system.core.controller;

import com.chatting.system.core.dto.ChatMessageDto;
import com.chatting.system.core.dto.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendRoomMessage")
    public ChatMessageDto sendRoomMessage(ChatMessageDto messageDto) {  // stomp의 payload(json)을 파라미터로 매핑
        log.info("Room Message:{}", messageDto);

        if (messageDto.getMessageType() == MessageType.ROOM) {
            // Room 기록
            String destination = "/topic/" + messageDto.getRoomId();
            messagingTemplate.convertAndSend(destination, messageDto);
        }
        return messageDto;
    }

    @MessageMapping("/chat.sendDirectMessage")
    public ChatMessageDto sendDirectMessage(ChatMessageDto messageDto) {
        log.info("Direct Message:{}", messageDto);

        if (messageDto.getMessageType() == MessageType.DIRECT) {
            // receiver 기록
            messagingTemplate.convertAndSend(
                    "/user/" + messageDto.getReceiver() + "/queue/messages",
                    messageDto
            );

            // sender 기록?
            messagingTemplate.convertAndSend(
                    "/user/" + messageDto.getSender() + "/queue/messages",
                    messageDto
            );
        }

        return messageDto;
    }

}
