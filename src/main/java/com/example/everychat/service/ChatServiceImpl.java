package com.example.everychat.service;

import com.example.everychat.domain.Channel;
import com.example.everychat.domain.Message;
import com.example.everychat.dto.MessageDto;
import com.example.everychat.dto.PagingChannelDto;
import com.example.everychat.dto.PagingMessageDto;
import com.example.everychat.enums.MessageTypeEnum;
import com.example.everychat.repository.ChannelRepository;
import com.example.everychat.repository.MessageRepository;
import com.example.everychat.util.AesUtil;
import com.example.everychat.vo.ChannelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private static HashMap<String, Integer> roomCount = new HashMap<>();

    @Transactional(readOnly = true)
    @Override
    public Object getChannelList(int page) {
        Page<Channel> channelList = channelRepository.findAllByOrderByCreateAtDesc(PageRequest.of(page, 10));
        PagingChannelDto pagingChannelDto = PagingChannelDto.builder()
                .channelList(channelList.getContent())
                .pageNumber(channelList.getNumber())
                .pageSize(channelList.getTotalPages())
                .build();

        return pagingChannelDto;
    }

    @Transactional
    @Override
    public void createChannel(ChannelVo channelVo) throws Exception {
        Channel channel = Channel.builder().id(UUID.randomUUID().toString())
                .channelName(channelVo.getChannelName())
                .ip(channelVo.getIp())
                .pw(AesUtil.encrypt(channelVo.getPw()))
                .createAt(LocalDateTime.now())
                .build();

        log.info("채널 생성 : " + channel.getId());

        log.info(channel.toString());

        channelRepository.save(channel);
    }

    @Transactional
    @Override
    public void sendMessage(MessageDto messageDto) {
        Message message = Message.builder().id(UUID.randomUUID().toString())
                .channelId(messageDto.getChannelId())
                .type(messageDto.getType())
                .sender(messageDto.getSender())
                .message(messageDto.getMessage())
                .ip(messageDto.getIp())
                .createAt(LocalDateTime.now())
                .build();

        int next = 1;
        if(messageDto.getType().equals(MessageTypeEnum.MESSAGE.getLabel())) {
            messageRepository.save(message);
            next = roomCount.get(messageDto.getChannelId());
        } else if(messageDto.getType().equals(MessageTypeEnum.ENTER.getLabel())) {
            System.out.println("enter " + messageDto.getSender());

            if(roomCount.containsKey(messageDto.getChannelId())) {
                next = roomCount.get(messageDto.getChannelId()) + 1;
                roomCount.replace(messageDto.getChannelId(), next);
            } else {
                roomCount.put(messageDto.getChannelId(), next);
            }
        } else {
            System.out.println("leave " + messageDto.getSender());

            next = roomCount.get(messageDto.getChannelId()) - 1;
            roomCount.replace(messageDto.getChannelId(), next);
        }
        message.setCount(next);

        simpMessageSendingOperations.convertAndSend("/topic/" + message.getChannelId(), message);
    }

    @Transactional(readOnly = true)
    @Override
    public Object getMessagePaging(String channelId, int page){
        Page<Message> messagePage = messageRepository.findAllByChannelIdOrderByCreateAtDesc(channelId, PageRequest.of(page, 10));
        PagingMessageDto pagingMessageDto = PagingMessageDto.builder()
                .messageList(messagePage.getContent().stream().sorted(Comparator.comparing(Message::getCreateAt)).collect(Collectors.toList()))
                .pageNumber(messagePage.getNumber())
                .pageSize(messagePage.getTotalPages())
                .build();
        return pagingMessageDto;
    }

    @Transactional
    @Override
    public boolean deleteRoom(String channelId, String pw) throws Exception {
        Channel channel = channelRepository.findById(channelId).get();

        if(pw.equals(AesUtil.decrypt(channel.getPw()))) {
            channelRepository.deleteById(channelId);

            log.info("채널 삭제 : " + channelId);
            return true;
        } else {
            log.info("채널 삭제 실패");
            return false;
        }


    }
}
