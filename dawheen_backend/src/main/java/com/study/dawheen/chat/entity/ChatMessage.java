package com.study.dawheen.chat.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


/**
 * MongoDB용 엔티티
 */

@Document(collation = "chat_messages")
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private Long id;
    private String name;
    private String chatRoomId;
    private LocalDateTime sentAt;
    // 어드민이 채팅친 것인지 확인
    private Boolean isAdmin;

}
