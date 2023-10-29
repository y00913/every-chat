package com.example.everychat.controller;

import com.example.everychat.dto.MessageDto;
import com.example.everychat.enums.MessageTypeEnum;
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
        if (message.getType().equals(MessageTypeEnum.MESSAGE.getLabel())) {
            chatService.sendMessage(message);
        } else {
            chatService.sendStatus(message);
        }
    }

    @GetMapping("/channel/{page}")
    public Object getChannelList(@PathVariable int page){
        Object data = chatService.getChannelList(page);
        return getResponseMessage(StatusEnum.OK, "채널 리스트", data);
    }

    @PostMapping("/channel")
    public Object createChannel(@RequestBody ChannelVo channelVo){
        chatService.createChannel(channelVo);

        return getResponseMessage(StatusEnum.OK, "채널 생성 완료");
    }

    @GetMapping("/message/{channelId}/{page}")
    public Object getMessage(@PathVariable String channelId, @PathVariable int page){
        Object messages = chatService.getMessagePaging(channelId, page);

        return getResponseMessage(StatusEnum.OK, "메세지 페이징 리스트", messages);
    }

}
