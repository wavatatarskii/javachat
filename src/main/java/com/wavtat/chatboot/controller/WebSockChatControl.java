package com.wavtat.chatboot.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.wavtat.chatboot.domain.WebSockChatMsg;

@Controller
public class WebSockChatControl {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/wavtat")
    public WebSockChatMsg sendMessage(@Payload WebSockChatMsg webSocketChatMessage) {
        return webSocketChatMessage;
    }

    @MessageMapping("/chat.newUser")
    @SendTo("/topic/wavtat")
    public WebSockChatMsg newUser(@Payload WebSockChatMsg webSocketChatMessage,
                                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", webSocketChatMessage.getSender());
        return webSocketChatMessage;
    }


}
