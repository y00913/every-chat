package com.example.everychat.service;

import com.example.everychat.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{


    @Override
    public Object getChannelList() {
        return null;
    }

    @Override
    public Object getMemberListByChannelId(String channelId) {
        return null;
    }

    @Override
    public void sendMessage(ChatDto chatDto) {

    }
}
