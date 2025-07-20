package com.chatting.system.core.subscriber;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final StreamListenerManager listenerManager;

    @EventListener
    public void handleConnet(SessionConnectEvent event) {
        String userId = getUserIdFromEvent(event);
        listenerManager.startListening(userId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String userId = getUserIdFromEvent(event);
        listenerManager.stopListening(userId);
    }

    private String getUserIdFromEvent(AbstractSubProtocolEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        return accessor.getUser().getName();
    }
}
