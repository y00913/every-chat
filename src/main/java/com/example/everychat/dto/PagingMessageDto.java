package com.example.everychat.dto;

import com.example.everychat.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingMessageDto {

    private List<Message> messageList;
    private int pageNumber;
    private int pageSize;

}
