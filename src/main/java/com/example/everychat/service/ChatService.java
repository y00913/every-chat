package com.example.everychat.service;

import com.example.everychat.dto.MessageDto;
import com.example.everychat.vo.ChannelVo;

public interface ChatService {

    Object getChannelList(int page);
    void createChannel(ChannelVo channelVo);
    void sendMessage(MessageDto messageDto);
    Object getMessagePaging(String channelId, int page);

}
