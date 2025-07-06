package com.chatting.system.core.dto;

/**
 *     ROOM,      // 채팅방 메시지
 *     DIRECT,    // 1:1 개인 메시지
 *     LOGOUT,    // 로그아웃 / 퇴장
 */
public enum MessageType {
    ROOM,
    DIRECT,
    LOGOUT,
}