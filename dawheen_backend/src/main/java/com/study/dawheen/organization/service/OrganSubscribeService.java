package com.study.dawheen.organization.service;

import com.study.dawheen.notification.dto.NotificationResponseDto;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.entity.OrganizationSubscribe;
import com.study.dawheen.organization.repository.OrganRepository;
import com.study.dawheen.organization.repository.OrganSubscribeRepository;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganSubscribeService {

    private final UserRepository userRepository;
    private final OrganRepository organRepository;
    private final OrganSubscribeRepository organSubscribeRepository;

    public void subscribe(String email, Long id) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        OrganizationSubscribe subscribe = OrganizationSubscribe.builder()
                .organization(organization)
                .user(user)
                .build();

        organSubscribeRepository.save(subscribe);
    }


}
