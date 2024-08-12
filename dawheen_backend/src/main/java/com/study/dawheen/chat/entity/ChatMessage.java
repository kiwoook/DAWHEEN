package com.study.dawheen.chat.entity;

import com.study.dawheen.chat.dto.ChatRoomCreateRequestDto;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


/**
 * MongoDB용 엔티티
 */

@Document(collection = "chat_messages")
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private String id;
    private String sender; //작성자
    private String chatRoomId; // 받는 채팅방 아이디
    private String message;
    private LocalDateTime sentAt;
    private ChatType chatType;

    @Builder
    public ChatMessage(String sender, String chatRoomId, String message, LocalDateTime sentAt, ChatType chatType) {
        this.sender = sender;
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.sentAt = sentAt;
        this.chatType = chatType;
    }


}
