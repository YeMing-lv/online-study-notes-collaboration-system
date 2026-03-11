package com.osnc.main.controller.websocket;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class MessageController {

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    // 处理来自客户端的消息，路径为/ws/message1
    @MessageMapping("/message1")
    // 将处理结果发送到/topic/messages1
    @SendTo("/topic/messages1")
    public String handleMessage1(String message) {
        log.info("/ws/message1 Get message:"+ message);
        return "Endpoint1: " + message;
    }

    // 处理来自客户端的消息，路径为/ws/message2
    @MessageMapping("/message2")
    // 将处理结果发送到/topic/messages2
    @SendTo("/topic/messages2")
    public String handleMessage2(String message) {
        return "Endpoint2: " + message;
    }

    // 处理来自客户端的消息，路径为/ws/message3
    @MessageMapping("/message3")
    // 将处理结果发送到/topic/messages3
    @SendTo("/topic/messages3")
    public String handleMessage3(String message) {
        return "Endpoint3: " + message;
    }
}
