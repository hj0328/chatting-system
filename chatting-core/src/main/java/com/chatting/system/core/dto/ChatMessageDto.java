package com.chatting.system.core.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class ChatMessageDto {
	private MessageType messageType;
	private String roomId;
	private String sender;
	private String receiver;
	private String content;
}
