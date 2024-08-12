package com.study.dawheen.chat.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomUpdateRequestDto {
    String title;

    public ChatRoomUpdateRequestDto(String title) {
        this.title = title;
    }
}
