package com.study.dawheen.chat.repository;

import com.study.dawheen.chat.dto.ChatRoomInfoResponseDto;

import java.util.List;

public interface ChatRoomUserRepositoryCustom {

    List<ChatRoomInfoResponseDto> findChatRoomByUserId(String userId);
}
