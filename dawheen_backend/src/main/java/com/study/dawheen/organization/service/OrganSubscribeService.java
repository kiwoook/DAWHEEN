package com.study.dawheen.organization.service;

import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.entity.OrganizationSubscribe;
import com.study.dawheen.organization.repository.OrganRepository;
import com.study.dawheen.organization.repository.OrganSubscribeRepository;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganSubscribeService {

    private final UserRepository userRepository;
    private final OrganRepository organRepository;
    private final OrganSubscribeRepository organSubscribeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.notification}")
    private String topic;

    public void subscribe(String email, Long id) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        OrganizationSubscribe subscribe = OrganizationSubscribe.builder()
                .organization(organization)
                .user(user)
                .build();

        organSubscribeRepository.save(subscribe);
    }

    public void cancel(String email, Long id) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        OrganizationSubscribe subscribe = organSubscribeRepository.findByUserAndOrganization(user, organization).orElseThrow(EntityNotFoundException::new);

        organSubscribeRepository.delete(subscribe);
    }

    // 구독자들에게 kafka 를 통해 메시지를 발행하도록 한다.
    public void sendNotify(Long id) {
        List<UserInfoResponseDto> userInfoResponseDtos = getUserByOrganization(id);

        String organizationName = organRepository.findById(id).orElseThrow(EntityNotFoundException::new).getName();

        String message = organizationName + "에서 봉사활동을 모집합니다!";

        for (UserInfoResponseDto userInfoResponseDto : userInfoResponseDtos) {
            String name = userInfoResponseDto.getName();
            ProducerRecord<String, Object> kafkaRecord = new ProducerRecord<>(topic, name, message);
            kafkaTemplate.send(kafkaRecord);
        }
    }


    // 특정 기관을 구독한 유저들의 정보를 반환
    private List<UserInfoResponseDto> getUserByOrganization(Long id) {
        List<UserInfoResponseDto> responseDtos = organSubscribeRepository.findUserByOrganizationId(id);

        if (responseDtos.isEmpty()) {
            throw new EmptyResultDataAccessException(0);
        }

        return responseDtos;
    }


}
