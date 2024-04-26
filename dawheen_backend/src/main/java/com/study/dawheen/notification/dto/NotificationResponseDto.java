package com.study.dawheen.notification.dto;

import com.study.dawheen.notification.entity.Notification;
import com.study.dawheen.user.entity.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link com.study.dawheen.notification.entity.Notification}
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationResponseDto {
    String content;
    UserDto receiver;
    Boolean isRead;


    public NotificationResponseDto(Notification notification) {
        this.content = notification.getContent();
        this.receiver = new UserDto(notification.getReceiver());
        this.isRead = notification.getIsRead();
    }



    @Getter
    @Data
    static class UserDto {
        private String email;

        public UserDto(User user) {
            this.email = user.getEmail();
        }
    }
}
