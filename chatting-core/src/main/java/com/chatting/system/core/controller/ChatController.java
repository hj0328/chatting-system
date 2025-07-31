package com.chatting.system.core.controller;

import com.chatting.system.core.dto.ChatMessageDto;
import com.chatting.system.core.dto.MessageType;
import com.chatting.system.core.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendRoomMessage")
    public ChatMessageDto sendRoomMessage(ChatMessageDto messageDto, Principal principal) {  // stomp의 payload(json)을 파라미터로 매핑
        log.info("Room Message:{} from user:{}", messageDto, principal.getName());

        if (messageDto.getMessageType() == MessageType.ROOM) {
            // Room 기록
//            String destination = "/topic/" + messageDto.getRoomId();
//            messagingTemplate.convertAndSend(destination, messageDto);

            chatMessageService.sendToRoom(messageDto);
        }
        return messageDto;
    }

    @MessageMapping("/chat.sendDirectMessage")
    public ChatMessageDto sendDirectMessage(ChatMessageDto messageDto, Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("Unauthenticated user tried to send message.");
        }

        log.info("Direct Message:{} from user:{}", messageDto, principal.getName());

        if (messageDto.getMessageType() == MessageType.DIRECT) {
            chatMessageService.sendDirect(messageDto);
            // receiver 기록
//            messagingTemplate.convertAndSend(
//                    "/user/" + messageDto.getReceiver() + "/queue/messages",
//                    messageDto
//            );
        }

        return messageDto;
    }

}
