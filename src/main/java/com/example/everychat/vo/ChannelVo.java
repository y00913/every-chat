package com.example.everychat.vo;

import com.example.everychat.annotation.ClientIp;
import lombok.Getter;

@Getter
public class ChannelVo {
    private String channelName;
    private String pw;
    @ClientIp
    private String ip;
    private Boolean isLock;
    private String lockPw;
}
