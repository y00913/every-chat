package com.example.everychat.controller;

import com.example.everychat.dto.MessageDto;
import com.example.everychat.enums.StatusEnum;
import com.example.everychat.service.ChatService;
import com.example.everychat.dto.ChannelDto;
import com.example.everychat.util.ClientUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
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
    public void sendMessage(MessageDto messageDto, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes){
        String clientIp = (String) sessionAttributes.get("clientIp");
        messageDto.setIp(clientIp);
        chatService.sendMessage(messageDto);
    }

    @GetMapping("/channel/{categoryId}/{page}")
    public Object getChannelList(@PathVariable Long categoryId, @PathVariable int page){
        Object data = chatService.getChannelList(page);
        return getResponseMessage(StatusEnum.OK, "채널 리스트", data);
    }

    @GetMapping("/channel/{categoryId}/{searchName}/{page}")
    public Object getChannelListByName(@PathVariable Long categoryId, @PathVariable String searchName, @PathVariable int page) {
        Object data = chatService.getChannelListByName(searchName, page);
        return getResponseMessage(StatusEnum.OK, "채널 검색 리스트", data);
    }

    @PostMapping("/channel")
    public Object createChannel(@RequestBody ChannelDto channelDto) throws Exception {
        Object data = chatService.createChannel(channelDto);

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

    @GetMapping("/uuid")
    public Object getUuid(HttpServletRequest request) {
        String ip = ClientUtil.getIp(request);
        String uuid = chatService.getUuid(ip);
        return getResponseMessage(StatusEnum.OK, "클라이언트 uuid", uuid);
    }

}
