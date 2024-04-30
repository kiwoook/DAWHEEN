package com.study.dawheen.notification.repository;

import com.study.dawheen.notification.dto.NotificationResponseDto;
import com.study.dawheen.notification.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepositoryCustom {
    Optional<List<NotificationResponseDto>> findAllByReceiver(String email);

    Optional<Notification> findByIdAndReceiverEmail(Long id, String email);

}
