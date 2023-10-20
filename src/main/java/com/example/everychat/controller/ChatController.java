package com.example.everychat.controller;

import com.example.everychat.dto.MessageDto;
import com.example.everychat.enums.StatusEnum;
import com.example.everychat.service.ChatService;
import com.example.everychat.vo.ChannelVo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import static com.example.everychat.enums.MakeResponse.*;

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
        Object data = chatService.getChannelList();
        return getResponseMessage(StatusEnum.OK, "채널 리스트", data);
    }

    @PostMapping("/channel")
    public Object createChannel(@RequestBody ChannelVo channelVo){
        chatService.createChannel(channelVo);

        return getResponseMessage(StatusEnum.OK, "채널 생성 완료");
    }

    @GetMapping("/message/{page}")
    public Object getMessage(@PathVariable int page){
        Object messages = chatService.getMessagePaging(page);

        return getResponseMessage(StatusEnum.OK, "메세지 페이징 리스트", messages);
    }

}
