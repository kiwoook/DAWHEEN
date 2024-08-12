package com.study.dawheen.chat.service;

import com.study.dawheen.chat.dto.ChatRoomInfoResponseDto;
import com.study.dawheen.chat.entity.ChatRoom;
import com.study.dawheen.chat.repository.ChatRoomRepository;
import com.study.dawheen.chat.repository.ChatRoomUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatQueryService {

    // TODO 쿼리 서비스 개발

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 사용자의 채팅방 가져오기
    public List<ChatRoomInfoResponseDto> getAllChatRoomsByUser(String userId) {
        List<ChatRoomInfoResponseDto> responseDtoList = chatRoomUserRepository.findChatRoomByUserId(userId);

        if (responseDtoList.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return responseDtoList;
    }

    // 특정 채팅방의 정보 가져오기
    public ChatRoomInfoResponseDto getChatRoom(Long chatRoomId) {

        // N + 1 문제 발생됨 (기존의 Volunteer 엔티티에 Lazy 로 들어가게 됨)
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(EntityNotFoundException::new);
        return new ChatRoomInfoResponseDto(chatRoom);
    }

    // 특정 채팅방의 내역 가져오기
    // 채팅방의 내역은 일주일치거나 최대 500개 정도로 제한한다.
    public void getChatMessageByChatRoom(Long chatRoomId) {

    }
}
