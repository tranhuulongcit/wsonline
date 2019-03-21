package com.jponline.wsonline.listener;

import com.jponline.wsonline.controller.WebSocketController;
import com.jponline.wsonline.model.Message;
import com.jponline.wsonline.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocketEventListener listen client connection and disconnect
 * Author: LongTH10
 */
@Component
public class WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        LOGGER.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        //notifi if user disconect
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        WebSocketController.listUser.remove(username);
        Notification notifi = new Notification();
        notifi.setData(WebSocketController.listUser);
        messagingTemplate.convertAndSend("/topic/notification", notifi);
    }

}
