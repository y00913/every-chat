package com.example.everychat.controller;

import com.example.everychat.dto.MessageDto;
import com.example.everychat.enums.StatusEnum;
import com.example.everychat.service.ChatService;
import com.example.everychat.vo.ChannelVo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.everychat.enums.MakeResponse.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    public void sendMessage(MessageDto message){
        System.out.println("check : " + message.getIp());
        chatService.sendMessage(message);
    }

    @GetMapping("/channel/{page}")
    public Object getChannelList(@PathVariable int page){
        Object data = chatService.getChannelList(page);
        return getResponseMessage(StatusEnum.OK, "채널 리스트", data);
    }

    @GetMapping("/channel/{searchName}/{page}")
    public Object getChannelListByName(@PathVariable String searchName, @PathVariable int page) {
        Object data = chatService.getChannelListByName(searchName, page);
        return getResponseMessage(StatusEnum.OK, "채널 검색 리스트", data);
    }

    @PostMapping("/channel")
    public Object createChannel(@RequestBody ChannelVo channelVo) throws Exception {
        Object data = chatService.createChannel(channelVo);

        return getResponseMessage(StatusEnum.OK, "채널 생성 완료", data);
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
        boolean check = chatService.checkLockPw(header.get("channel-id"), header.get("lock-pw"));

        return getResponseMessage(StatusEnum.OK, "비밀번호 확인 유무", check);
    }

    @GetMapping("/channel/lock/{channelId}")
    public Object checkLockVerify(@PathVariable String channelId) {
        boolean check = chatService.checkLockVerify(channelId);

        return getResponseMessage(StatusEnum.OK, "비밀번호 세션 확인 유무", check);
    }

    @GetMapping("/channel/name/{name}")
    public Object checkExistName(@PathVariable String name) {
        Object data = chatService.checkExistName(name);

        return getResponseMessage(StatusEnum.OK, "방 이름 존재 유무", data);
    }

}
