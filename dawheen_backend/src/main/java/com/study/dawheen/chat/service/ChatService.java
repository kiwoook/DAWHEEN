package com.study.dawheen.chat.service;

import com.study.dawheen.chat.dto.ChatMessageDto;
import com.study.dawheen.chat.dto.ChatRoomCreateRequestDto;
import com.study.dawheen.chat.dto.ChatRoomUpdateRequestDto;
import com.study.dawheen.chat.entity.ChatMessage;
import com.study.dawheen.chat.entity.ChatRoom;
import com.study.dawheen.chat.repository.ChatMessageRepository;
import com.study.dawheen.chat.repository.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    // 메시지 저장하기
    public void saveMessage(ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = ChatMessage.builder()
                .message(chatMessageDto.getMessage())
                .chatRoomId(chatMessageDto.getChatRoomId())
                .chatType(chatMessageDto.getChatType())
                .sender(chatMessageDto.getSender())
                .sentAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);
    }

    // 메시지 삭제하기
    public void deleteMessage(String messageId) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId).orElseThrow(EntityNotFoundException::new);
        chatMessageRepository.delete(chatMessage);
    }

    // 채팅방 생성하기
    public void create(ChatRoomCreateRequestDto requestDto) {
        chatRoomRepository.save(ChatRoom.toEntity(requestDto));
    }

    // 채팅방 삭제하기
    public void delete(Long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
    }

    // 채팅방 수정하기
    public void modify(Long chatRoomId, ChatRoomUpdateRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(EntityNotFoundException::new);
        chatRoom.update(requestDto);
    }

}
