package com.jponline.wsonline.config;

import com.jponline.wsonline.controller.UploadWSHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocketConfig config websocket message broker
 * Author: LongTH10
 */
@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig  extends AbstractWebSocketMessageBrokerConfigurer implements WebSocketConfigurer  {

    @Autowired
    private HttpHandshakeInterceptor handshakeInterceptor;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //handle upload file ws
        registry.addHandler(new UploadWSHandler(), "/binary");

    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        //set file size limit
        registration.setMessageSizeLimit(50 * 1024 * 1024);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //set endpoint and allow origin
        //support browser not support websocket
        //set interceptor handshake serverlet get the Session of request
        registry.addEndpoint("/jponline").setAllowedOrigins("*").withSockJS().setInterceptors(handshakeInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                //configure Channel for user send message one to one
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String user = accessor.getFirstNativeHeader("user");
                    if (!StringUtils.isEmpty(user)) {
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                        Authentication auth = new UsernamePasswordAuthenticationToken(user, user, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        accessor.setUser(auth);
                    }
                }
                return message;
            }
        });
    }
}




