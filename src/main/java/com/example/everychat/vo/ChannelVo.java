package com.example.everychat.vo;

import lombok.Getter;

@Getter
public class ChannelVo {
    private String channelName;
    private String pw;
    private String ip;
    private Boolean isLock;
    private String lockPw;
}
