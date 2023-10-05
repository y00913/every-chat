package com.example.everychat.service;

import com.example.everychat.dto.MessageDto;
import com.example.everychat.vo.ChannelVo;

public interface ChatService {

    Object getChannelList();
    void createChannel(ChannelVo channelVo);
    void sendMessage(MessageDto messageDto);

}
