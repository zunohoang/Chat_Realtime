package org.example.chatwebsocket.controllers;

import lombok.AllArgsConstructor;
import org.example.chatwebsocket.models.Message;
import org.example.chatwebsocket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.awt.print.Printable;
import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class MessageController {

    private SimpMessagingTemplate simpMessagingTemplate;
    private UserService userService;

    @MessageMapping("/chat")
    public void chat(@Payload Message message, Principal principal) {
        message.setFromUser(principal.getName());
        System.out.println("Message: " + message);
        simpMessagingTemplate.convertAndSendToUser(message.getToUser(), "/topic/message", message);
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        String username = event.getUser().getName();
        simpMessagingTemplate.convertAndSend("/topic/online", username);
        userService.setStatus(username, "ON");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = event.getUser().getName();
        simpMessagingTemplate.convertAndSend("/topic/offline", username);
        userService.setStatus(username, "OF");
    }


    @MessageMapping("/getOnlineUsers")
    public void getOnlineUsers(@Payload String message, Principal principal) {
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/topic/onlineUsers", userService.getOnlineUsers());
    }

}
