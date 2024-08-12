package com.study.dawheen.chat.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateRequestDto {

    private String title;
    private String volunteerWorkId;

    public ChatRoomCreateRequestDto(String title, String volunteerWorkId) {
        this.title = title;
        this.volunteerWorkId = volunteerWorkId;
    }
}
