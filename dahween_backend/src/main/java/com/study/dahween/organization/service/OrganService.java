package com.study.dahween.organization.service;

import com.study.dahween.organization.dto.OrganInfoResponseDto;
import com.study.dahween.organization.dto.OrganRequestDto;
import com.study.dahween.organization.entity.Organization;
import com.study.dahween.organization.repository.OrganRepository;
import com.study.dahween.user.dto.UserInfoResponseDto;
import com.study.dahween.user.entity.User;
import com.study.dahween.user.repository.UserRepository;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import com.study.dahween.volunteer.repository.UserVolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganService {

    private final OrganRepository organRepository;
    private final UserRepository userRepository;
    private final UserVolunteerRepository userVolunteerRepository;


    public OrganInfoResponseDto getOrgan(Long id) throws EntityNotFoundException {
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new OrganInfoResponseDto(organization);
    }

    public List<OrganInfoResponseDto> getPendingOrganList() throws EntityNotFoundException {
        return organRepository.getAllByApproved(false).orElseThrow(EntityNotFoundException::new).stream().map(OrganInfoResponseDto::new).toList();
    }


    @Transactional
    public OrganInfoResponseDto create(OrganRequestDto requestDto) {
        Organization organization = Organization.toEntity(requestDto);
        Organization savedOrgan = organRepository.save(organization);

        return new OrganInfoResponseDto(savedOrgan);
    }

    @Transactional
    public void delete(Long id) throws EmptyResultDataAccessException, EntityNotFoundException {
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        List<User> userList = userRepository.findAllByOrganization(organization).orElseThrow(EntityNotFoundException::new);

        for (User user : userList) {
            log.info("유저 아이디 : {}의 기관 정보를 삭제합니다.", user.getUserId());
            user.associateOrganization(null);
        }

        log.info("기관 ID : {}인 엔티티 삭제", id);
        organRepository.delete(organization);
    }

    @Transactional
    public void update(Long id, OrganRequestDto requestDto) throws EntityNotFoundException {
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        organization.update(requestDto);
    }

    public boolean verifyAffiliation(Long id, String userId) throws EntityNotFoundException {
        User user = userRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return user.getOrganization().equals(organization);
    }

    @Transactional
    public void enroll(Long id) {
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (Boolean.TRUE.equals(organization.getApproved())) {
            organization.approved();
        } else {
            throw new IllegalStateException();
        }
    }

    @Transactional
    public void denied(Long id) {
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (Boolean.FALSE.equals(organization.getApproved())) {
            organRepository.delete(organization);
        } else {
            throw new IllegalStateException();
        }
    }

    @Transactional
    public void grantOrganizationRole(String userId, Long organId) {
        Organization organization = organRepository.findById(organId).orElseThrow(EntityNotFoundException::new);
        if (Boolean.FALSE.equals(organization.getApproved())) {
            throw new IllegalStateException();
        }
        User user = userRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        organization.addUser(user);
        user.grantOrganization(organization);
    }

    @Transactional
    public void revokeOrganizationRole(String userId, Long organId) {
        User user = userRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        Organization organization = organRepository.findById(organId).orElseThrow(EntityNotFoundException::new);

        user.revokeOrganization();
        organization.revokeUser(user);
    }

    public List<OrganInfoResponseDto> findOrganizationsWithinRadius(double latitude, double longitude, int radius) {

        List<Organization> organizationList = organRepository.findOrganizationsWithinRadius(latitude, longitude, radius).orElseThrow(EntityNotFoundException::new);

        return organizationList.stream().map(OrganInfoResponseDto::new).toList();
    }

}
