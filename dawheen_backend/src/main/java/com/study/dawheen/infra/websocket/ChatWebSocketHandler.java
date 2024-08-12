package com.study.dawheen.infra.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dawheen.chat.dto.ChatMessageDto;
import com.study.dawheen.chat.service.ChatService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final ChatService chatService;

    // 현재 연결된 세션들
    private final Set<WebSocketSession> sessions = new HashSet<>();

    private final Map<String, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 세션 연결", session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        // 메시지 처리 로직 구현
        String payload = message.getPayload();
        log.info("payload {}", payload);
        ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);
        String chatRoomId = chatMessageDto.getChatRoomId();
        if (!chatRoomSessionMap.containsKey(chatRoomId)) {
            chatRoomSessionMap.put(chatRoomId, new HashSet<>());
        }
        Set<WebSocketSession> webSocketSessions = chatRoomSessionMap.get(chatRoomId);


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 세션 연결", session.getId());
        sessions.remove(session);
    }
}
