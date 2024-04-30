package com.study.dawheen.infra.kafka;

import com.study.dawheen.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${spring.kafka.topic.notification}", groupId = "${spring.kafka.consumer.group-id}")
    public void notifySend(ConsumerRecord<String, Object> consumerRecord) {
        String receiverEmail = consumerRecord.key();
        String content = consumerRecord.value().toString();
        notificationService.send(receiverEmail, content);
    }
}
