package com.example.everychat.controller;

import com.example.everychat.dto.MessageDto;
import com.example.everychat.enums.StatusEnum;
import com.example.everychat.service.ChatService;
import com.example.everychat.vo.ChannelVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/channel/{page}")
    public Object getChannelList(@PathVariable int page){
        Object data = chatService.getChannelList(page);
        return getResponseMessage(StatusEnum.OK, "채널 리스트", data);
    }

    @PostMapping("/channel")
    public Object createChannel(@RequestBody ChannelVo channelVo) throws Exception {
        chatService.createChannel(channelVo);

        return getResponseMessage(StatusEnum.OK, "채널 생성 완료");
    }

    @GetMapping("/message/{channelId}/{page}")
    public Object getMessage(@PathVariable String channelId, @PathVariable int page){
        Object messages = chatService.getMessagePaging(channelId, page);

        return getResponseMessage(StatusEnum.OK, "메세지 페이징 리스트", messages);
    }

    @DeleteMapping("/channel")
    public Object deleteRoom(@RequestHeader Map<String, String> header) throws Exception {
        String message = "비밀번호가 틀렸습니다.";
        if(chatService.deleteRoom(header.get("channel-id"), header.get("pw"))) {
            message = "채널 삭제 완료";
        }

        return getResponseMessage(StatusEnum.OK, message, null);
    }

    @GetMapping("/channel/lock")
    public Object checkLockPw(@RequestHeader Map<String, String> header) throws Exception {
        boolean tf = chatService.checkLockPw(header.get("channel-id"), header.get("lock-pw"));

        return getResponseMessage(StatusEnum.OK, "비밀번호 확인 유무", tf);
    }

}
