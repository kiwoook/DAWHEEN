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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void cancel(String email, Long id) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        OrganizationSubscribe subscribe = organSubscribeRepository.findByUserAndOrganization(user, organization).orElseThrow(EntityNotFoundException::new);

        organSubscribeRepository.delete(subscribe);
    }


    // TODO 봉사활동 만들어지면 연결되도록
    // 특정 기관을 구독한 유저들의 정보를 반환
    public List<UserInfoResponseDto> getUserByOrganization(Long id) {
        List<UserInfoResponseDto> responseDtos = organSubscribeRepository.findUserByOrganizationId(id);

        if (responseDtos.isEmpty()) {
            throw new EmptyResultDataAccessException(0);
        }

        return responseDtos;
    }


}
