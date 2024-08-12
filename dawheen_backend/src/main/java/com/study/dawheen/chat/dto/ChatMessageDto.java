package com.study.dawheen.chat.dto;

import com.study.dawheen.chat.entity.ChatMessage;
import com.study.dawheen.chat.entity.ChatType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageDto {

    private String sender; //작성자
    private String chatRoomId; // 받는 채팅방 아이디
    private String message;
    private LocalDateTime sentAt;
    private ChatType chatType;

    public ChatMessageDto(ChatMessage chatMessage) {
        this.sender = chatMessage.getSender();
        this.chatRoomId = chatMessage.getChatRoomId();
        this.message = chatMessage.getMessage();
        this.sentAt = chatMessage.getSentAt();
        this.chatType = chatMessage.getChatType();
    }
}
