package com.study.dawheen.notification.entity;

import com.study.dawheen.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_EMAIL", referencedColumnName = "EMAIL", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private Boolean isRead;

    @Builder
    public Notification(String content, User receiver) {
        this.content = content;
        this.receiver = receiver;
        this.isRead = false;
    }

    public void hadRead(){
        this.isRead = true;
    }
}
