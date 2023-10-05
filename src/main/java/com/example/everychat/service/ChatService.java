package com.example.everychat.service;

import com.example.everychat.dto.ChatDto;

public interface ChatService {

    Object getChannelList();
    Object getMemberListByChannelId(String channelId);
    void sendMessage(ChatDto chatDto);

}
