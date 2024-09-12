package com.study.dawheen.chat.repository;

import com.study.dawheen.chat.entity.ChatMessage;
import com.study.dawheen.chat.entity.ChatType;
import com.study.dawheen.config.TestMongoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataMongoTest
@Import({TestMongoConfig.class})
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class ChatMessageJpaRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setUp() {
        chatMessageRepository.deleteAll();
    }

    @Test
    void testSaveAndFindChatMessage() {
        // Given
        ChatMessage chatMessage = ChatMessage.builder()
                .sender("user1")
                .chatRoomId("room1")
                .message("Hello, World!")
                .sentAt(LocalDateTime.now())
                .chatType(ChatType.USER)
                .build();

        // When
        chatMessageRepository.save(chatMessage);
        List<ChatMessage> foundMessages = chatMessageRepository.findByChatRoomId("room1");

        // Then
        assertThat(foundMessages).isNotEmpty();
        assertThat(foundMessages.get(0).getMessage()).isEqualTo("Hello, World!");

        chatMessageRepository.delete(chatMessage);
    }

    @Test
    void testFindByChatRoomId() {
        // Given
        ChatMessage chatMessage1 = ChatMessage.builder()
                .sender("user1")
                .chatRoomId("room1")
                .message("First message")
                .sentAt(LocalDateTime.now())
                .chatType(ChatType.USER)
                .build();
        ChatMessage chatMessage2 = ChatMessage.builder()
                .sender("user2")
                .chatRoomId("room1")
                .message("Second message")
                .sentAt(LocalDateTime.now())
                .chatType(ChatType.USER)
                .build();

        chatMessageRepository.save(chatMessage1);
        chatMessageRepository.save(chatMessage2);

        // When
        List<ChatMessage> foundMessages = chatMessageRepository.findByChatRoomId("room1");

        // Then
        assertThat(foundMessages).hasSize(2);
        assertThat(foundMessages).extracting(ChatMessage::getMessage)
                .containsExactlyInAnyOrder("First message", "Second message");

        chatMessageRepository.delete(chatMessage1);
        chatMessageRepository.delete(chatMessage2);
    }
}