package com.example.everychat.service;

import com.example.everychat.dto.MessageDto;
import com.example.everychat.dto.ChannelDto;

public interface ChatService {

    Object getChannelList(int page);
    Object getChannelListByName(String searchName,int page);
    Object createChannel(ChannelDto channelDto) throws Exception;
    void sendMessage(MessageDto messageDto);
    Object getMessagePaging(String channelId, int page);
    boolean deleteRoom(String channelId, String pw) throws Exception;
    boolean checkLockPw(String channelId, String pw) throws Exception;
    boolean checkLockVerify(String channelId);
    void incrementCount(String channelId);
    void subtractCount(String channelId);
    void sendCount(String channelId) throws InterruptedException;
    boolean checkExistName(String name);
    String getUuid(String ip);

}
