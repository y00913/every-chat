package com.example.everychat.service;

import com.example.everychat.domain.Channel;
import com.example.everychat.domain.ChannelLock;
import com.example.everychat.domain.Message;
import com.example.everychat.dto.MessageDto;
import com.example.everychat.dto.PagingChannelDto;
import com.example.everychat.dto.PagingMessageDto;
import com.example.everychat.enums.MessageTypeEnum;
import com.example.everychat.repository.ChannelLockRepository;
import com.example.everychat.repository.ChannelRepository;
import com.example.everychat.repository.MessageRepository;
import com.example.everychat.util.AesUtil;
import com.example.everychat.dto.ChannelDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ChannelLockRepository channelLockRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final HttpServletRequest request;
    private static final ConcurrentHashMap<String, Integer> memberCount = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    @Override
    public Object getChannelList(int page) {
        Page<Channel> channelPage = channelRepository.findAllByOrderByCreateAtDesc(PageRequest.of(page, 10));

        List<Channel> channels = channelPage.getContent();
        channels.forEach(channel -> {
            Integer memberCount = getCount(channel.getId());
            channel.setMemberCount(memberCount != null ? memberCount : 0);
        });

        PagingChannelDto pagingChannelDto = PagingChannelDto.builder()
                .channelList(channelPage.getContent())
                .pageNumber(channelPage.getNumber())
                .pageSize(channelPage.getTotalPages())
                .build();

        return pagingChannelDto;
    }

    @Transactional(readOnly = true)
    @Override
    public Object getChannelListByName(String searchName, int page) {
        Page<Channel> channelPage = channelRepository.findAllByChannelNameContainsOrderByCreateAtDesc(searchName, PageRequest.of(page, 10));

        List<Channel> channels = channelPage.getContent();
        channels.forEach(channel -> {
            Integer memberCount = getCount(channel.getId());
            channel.setMemberCount(memberCount != null ? memberCount : 0);
        });

        PagingChannelDto pagingChannelDto = PagingChannelDto.builder()
                .channelList(channelPage.getContent())
                .pageNumber(channelPage.getNumber())
                .pageSize(channelPage.getTotalPages())
                .build();

        return pagingChannelDto;
    }

    private Integer getCount(String channelId) {
        return memberCount.get(channelId);
    }

    @Transactional
    @Override
    public Object createChannel(ChannelDto channelDto) throws Exception {
        Channel channel = Channel.builder().id(UUID.randomUUID().toString())
                .channelName(channelDto.getChannelName())
                .ip(channelDto.getIp())
                .pw(AesUtil.encrypt(channelDto.getPw()))
                .isLock(channelDto.getIsLock())
                .createAt(LocalDateTime.now())
                .build();

        channelRepository.save(channel);

        if(channel.getIsLock()){
            ChannelLock channelLock = ChannelLock.builder()
                    .channelId(channel.getId())
                    .lockPw(AesUtil.encrypt(channelDto.getLockPw()))
                    .build();

            channelLockRepository.save(channelLock);
        }

        return channel;
    }

    @Transactional
    @Override
    public void sendMessage(MessageDto messageDto) {
        if(messageDto.getType().equals(MessageTypeEnum.MESSAGE.getLabel())) {
            Message message = Message.builder().id(UUID.randomUUID().toString())
                .channelId(messageDto.getChannelId())
                .type(messageDto.getType())
                .sender(messageDto.getSender())
                .message(messageDto.getMessage())
                .ip(messageDto.getIp())
                .createAt(messageDto.getCreateAt())
                .build();

            messageRepository.save(message);
        } else if(messageDto.getType().equals(MessageTypeEnum.ENTER.getLabel())) {
//            log.info("channelId : {}", messageDto.getChannelId());
//            log.info("enter : {}", messageDto.getSender());
        } else {
//            log.info("channelId : {}", messageDto.getChannelId());
//            log.info("leave : {}", messageDto.getSender());
        }

        String ip = messageDto.getIp();
        messageDto.setIp(ip.substring(0, ip.indexOf('.', 5)));

        simpMessageSendingOperations.convertAndSend("/topic/" + messageDto.getChannelId(), messageDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Object getMessagePaging(String channelId, int page){
        Page<Message> messagePage = messageRepository.findAllByChannelIdOrderByCreateAtDesc(channelId, PageRequest.of(page, 10));
        messagePage.stream().forEach(message -> {
            String ip = message.getIp();
            message.setIp(ip.substring(0, ip.indexOf('.', 5)));
        });
        PagingMessageDto pagingMessageDto = PagingMessageDto.builder()
                .messageList(messagePage.getContent().stream().sorted(Comparator.comparing(Message::getCreateAt)).collect(Collectors.toList()))
                .pageNumber(messagePage.getNumber())
                .pageSize(messagePage.getTotalPages())
                .build();
        return pagingMessageDto;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkLockPw(String channelId, String lockPw) throws Exception {
        ChannelLock channelLock = channelLockRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("channelLock doesn't exist"));

        if(lockPw.equals(AesUtil.decrypt(channelLock.getLockPw()))) {
            HttpSession session = request.getSession();

            String attributeName = "channelLockSuccess_" + channelId;
            session.setAttribute(attributeName, true);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkLockVerify(String channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("channel doesn't exist"));

        if(!channel.getIsLock()) return true;

        HttpSession session = request.getSession();

        String attributeName = "channelLockSuccess_" + channelId;
        Object passwordChecked = session.getAttribute(attributeName);

        if(passwordChecked == null) return false;

        return Boolean.parseBoolean(passwordChecked.toString());
    }

    @Transactional
    @Override
    public boolean deleteRoom(String channelId, String pw) throws Exception {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("channel doesn't exist"));

        if(pw.equals(AesUtil.decrypt(channel.getPw()))) {
            channel.setDeleteAt(LocalDateTime.now());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void incrementCount(String channelId) {
        memberCount.merge(channelId, 1, Integer::sum);
    }

    @Override
    public void subtractCount(String channelId) {
        memberCount.merge(channelId, 0, (oldValue, newValue) -> oldValue + newValue - 1);
    }

    @Override
    public void sendCount(String channelId) throws InterruptedException {
        Message message = Message.builder().id(UUID.randomUUID().toString())
                .channelId(channelId)
                .type("count")
                .message(memberCount.get(channelId).toString())
                .createAt(LocalDateTime.now())
                .build();
        Thread.sleep(100);
        simpMessageSendingOperations.convertAndSend("/topic/" + channelId, message);
    }

    @Transactional(readOnly = true)
    public boolean checkExistName(String name) {
        return channelRepository.existsByChannelName(name);
    }

}
