package com.example.everychat.dto;

import com.example.everychat.domain.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingChannelDto {

    private List<Channel> channelList;
    private int pageNumber;
    private int pageSize;

}
