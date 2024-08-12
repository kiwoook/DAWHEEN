package com.study.dawheen.chat.entity;

import com.study.dawheen.chat.dto.ChatRoomCreateRequestDto;
import com.study.dawheen.chat.dto.ChatRoomUpdateRequestDto;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Table(name = "CHATROOM")
@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @OneToOne(mappedBy = "chatRoom", orphanRemoval = true, cascade = CascadeType.ALL)
    private VolunteerWork volunteerWork;

    private String title;

    @Builder
    public ChatRoom(String title) {
        this.title = title;
    }

    public static ChatRoom toEntity(ChatRoomCreateRequestDto requestDto) {
        return ChatRoom.builder()
                .title(requestDto.getTitle())
                .build();
    }

    public void update(ChatRoomUpdateRequestDto requestDto){
        this.title = requestDto.getTitle();
    }

}
