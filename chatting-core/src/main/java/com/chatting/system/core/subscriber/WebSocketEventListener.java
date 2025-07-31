package com.chatting.system.core.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final StreamListenerManager listenerManager;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        String userId = getUserIdFromEvent(event);
        listenerManager.startListening(userId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String userId = getUserIdFromEvent(event);
        listenerManager.stopListening(userId);
    }

    private String getUserIdFromEvent(AbstractSubProtocolEvent event) {
        Principal user = event.getUser();
        log.info("EventListener user principal:{}", user);

        if (user != null) {
            listenerManager.startListening(user.getName());
        } else {
            log.warn("user is null in SessionConnectedEvent");
        }

        return user.getName();
    }
}
