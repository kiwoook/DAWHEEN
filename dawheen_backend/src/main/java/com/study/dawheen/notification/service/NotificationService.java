package com.study.dawheen.notification.service;

import com.study.dawheen.notification.dto.NotificationResponseDto;
import com.study.dawheen.notification.entity.Notification;
import com.study.dawheen.notification.repository.EmitterRepository;
import com.study.dawheen.notification.repository.NotificationRepository;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final UserRepository userRepository;

    public SseEmitter subscribe(String email, String lastEventId) {
        String emitterId = makeTimeIncludeId(email);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        String eventId = makeTimeIncludeId(lastEventId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userEmail=" + email + "]");

        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, email, emitterId, emitter);
        }

        return emitter;
    }

    public void send(String email, String content){
        User receiver = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        Notification notification = Notification.builder()
                .content(content)
                .receiver(receiver)
                .build();

        notificationRepository.save(notification);

        String eventId = makeTimeIncludeId(email);
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(email);
        emitters.forEach(
                (key, emitter) ->{
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, new NotificationResponseDto(notification));
                }
        );

    }

    private String makeTimeIncludeId(String email) {
        return email + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data)
            );
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userEmail, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(userEmail));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

}
