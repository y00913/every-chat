package com.example.everychat.controller;

import com.example.everychat.dto.MessageDto;
import com.example.everychat.service.ChatService;
import com.example.everychat.vo.ChannelVo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    public void sendMessage(MessageDto message){
        chatService.sendMessage(message);
    }

    @GetMapping("/channel")
    public Object getChannelList(){
        return chatService.getChannelList();
    }

    @PostMapping("/channel")
    public Object createChannel(ChannelVo channelVo){
        chatService.createChannel(channelVo);

        return "ok";
    }

}
