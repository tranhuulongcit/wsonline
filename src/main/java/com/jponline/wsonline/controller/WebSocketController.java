package com.jponline.wsonline.controller;

import com.google.gson.Gson;
import com.jponline.wsonline.model.Message;
import com.jponline.wsonline.model.Notification;
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

import java.awt.*;
import java.security.Principal;
import java.util.*;
import java.util.List;

/**
 * WebSocketController handle request client
 * Author: LongTH10
 */
@Controller
public class WebSocketController {

    Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);
    public static HashMap<String, String> listUser = new HashMap<>();
    public static List<String> listUserLogin = new ArrayList<>();

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
        String sessionID = headerAccessor.getSessionAttributes().get("sessionId").toString();
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        WebSocketController.listUser.put(message.getSender(), sessionID);
        LOGGER.info(listUser.toString());
        boolean isMatch = false;
        for(String item : listUserLogin) {
            if (!item.equals(sessionID)) {
                isMatch = false;
            } else {
                isMatch = true;
                break;
            }
        }

        if (!isMatch) {
            listUserLogin.add(sessionID);
        } else {
            message.setFist(false);
        }
        Notification notifi = new Notification();
        notifi.setMessage("new have user login");
        notifi.setData(WebSocketController.listUser);
        messagingTemplate.convertAndSend("/topic/notification", notifi);
        return message;
    }


    @MessageMapping("/chat.listUser")
    @SendTo("/topic/notification")
    public Notification listUser(@Payload Notification notification) {
        return notification;
    }


    @MessageMapping("/sendTo")
    public void conversation(@Payload Message message, Principal principal) throws Exception {
        //send message to user channel in bound
        messagingTemplate.convertAndSendToUser(message.getToUser(), "/queue/reply", message);
    }

}
