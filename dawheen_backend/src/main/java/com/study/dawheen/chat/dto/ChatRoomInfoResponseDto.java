package com.study.dawheen.chat.dto;

import com.study.dawheen.chat.entity.ChatRoom;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomInfoResponseDto {

    private Long chatRoomId;
    private String title;
    private AtomicInteger participants; // 인원 수

    public ChatRoomInfoResponseDto(ChatRoom chatRoom) {
        this.chatRoomId = chatRoom.getId();
        this.title = chatRoom.getTitle();
        this.participants = chatRoom.getVolunteerWork().getAppliedParticipants();
    }
}
