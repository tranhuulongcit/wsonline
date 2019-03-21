package com.jponline.wsonline.controller;

import com.google.gson.Gson;
import com.jponline.wsonline.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class WebSocketController {

    Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Gson gson = new Gson();

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/publicChatRoom")
    public Message sendMessage(@Payload Message chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/publicChatRoom")
    public Message addUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        return message;
    }

    @MessageMapping("/sendTo")
    public void conversation(@Payload Message message, Principal principal) throws Exception {
        messagingTemplate.convertAndSendToUser(message.getToUser(), "/queue/reply", message);
    }

//    @MessageExceptionHandler
//    @SendToUser("/queue/errors")
//    public String handleException(Throwable exception) {
//        return exception.getMessage();
//    }

}
