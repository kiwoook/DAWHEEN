package com.study.dawheen.volunteer.service;

import com.study.dawheen.common.exception.AlreadyProcessedException;
import com.study.dawheen.common.exception.AuthorizationFailedException;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.repository.OrganRepository;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerCreateRequestDto;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.dto.VolunteerUpdateResponseDto;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolunteerService {

    private final VolunteerWorkRepository volunteerWorkRepository;
    private final UserVolunteerRepository userVolunteerRepository;
    private final UserRepository userRepository;


    // TODO 봉사활동이 만들어졌다면 특정 기관을 구독한 유저에게 알림이 전송되어야함. KAFKA 활용?
    @Transactional
    public VolunteerInfoResponseDto create(String email, VolunteerCreateRequestDto createResponseDto) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        VolunteerWork volunteerWork = VolunteerWork.toEntity(createResponseDto);
        Organization organization = user.getOrganization();

        if (organization != null) {
            volunteerWork.updateOrganization(organization);
        }
        VolunteerWork savedVolunteerWork = volunteerWorkRepository.save(volunteerWork);

        return new VolunteerInfoResponseDto(savedVolunteerWork);
    }

    @Transactional
    public void delete(Long volunteerWorkId) throws EmptyResultDataAccessException, EntityNotFoundException {
        List<UserVolunteerWork> userVolunteerWorks = userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(volunteerWorkId).orElseThrow(EntityNotFoundException::new);

        for (UserVolunteerWork userVolunteerWork : userVolunteerWorks) {
            User user = userVolunteerWork.getUser();
            user.leaveVolunteerWork(userVolunteerWork);
        }

        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);
        volunteerWorkRepository.delete(volunteerWork);
    }


    @Transactional
    public VolunteerInfoResponseDto update(Long volunteerWorkId, VolunteerUpdateResponseDto updateResponseDto) {
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);
        volunteerWork.update(updateResponseDto);

        return new VolunteerInfoResponseDto(volunteerWork);
    }

    // 대기중인 유저를 확인해 신청할지 안할지 정할 수 있음.
    // 소속된 기관의 유저 리스트 반환


    @Transactional
    public void apply(Long volunteerWorkId, String email) {

        if (userVolunteerRepository.existsByVolunteerWorkAndUserAndStatus(volunteerWorkId, email, List.of(ApplyStatus.APPROVED, ApplyStatus.PENDING))) {
            log.info("이미 신청한 아이디 email = {}, volunteerWorkId = {}", email, volunteerWorkId);
            throw new AlreadyProcessedException();
        }

        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(volunteerWork.getRecruitEndDateTime())) {
            throw new IllegalStateException();
        }

        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        userVolunteerRepository.save(userVolunteerWork);
    }


    @Transactional
    public void approve(Long volunteerWorkId, String email) throws IllegalAccessException {
        // 승인권한은 해당 기관담당만 가능하도록 해야함.
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndEmail(volunteerWorkId, email).orElseThrow(EntityNotFoundException::new);
        VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();
        String requestEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Organization organization = userRepository.findByEmail(requestEmail).orElseThrow(EntityNotFoundException::new).getOrganization();

        if (!volunteerWork.getOrganization().equals(organization)) {
            throw new AuthorizationFailedException("you are not allowed to approve method");
        }

        if (volunteerWork.getAppliedParticipants().get() >= volunteerWork.getMaxParticipants()) {
            throw new IllegalAccessException();
        }

        User user = userVolunteerWork.getUser();
        user.attendVolunteerWork(userVolunteerWork);
        volunteerWork.attendUser(userVolunteerWork);
        volunteerWork.increaseParticipants();
    }

    @Transactional
    public void cancelPending(Long volunteerWorkId, String email) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndEmail(volunteerWorkId, email).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);
    }


    public void cancelApproved(Long volunteerWorkId, String email) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndEmail(volunteerWorkId, email).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.APPROVED) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);

        User user = userVolunteerWork.getUser();
        VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();

        user.leaveVolunteerWork(userVolunteerWork);
        volunteerWork.leaveUser(userVolunteerWork);
    }

    @Transactional
    public void cancelPendingForOrganization(Long volunteerWorkId, String email) {
        String requestEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (equalOrganizationByUserAndVolunteerWork(requestEmail, volunteerWorkId)) {
            throw new AuthorizationFailedException("you are not allowed to approve method");
        }

        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndEmail(volunteerWorkId, email).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);
    }

    @Transactional
    public void cancelApprovedForOrganization(Long volunteerWorkId, String email) {
        String requestEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (equalOrganizationByUserAndVolunteerWork(requestEmail, volunteerWorkId)) {
            throw new AuthorizationFailedException("you are not allowed to approve method");
        }

        cancelApproved(volunteerWorkId, email);
    }

    private boolean equalOrganizationByUserAndVolunteerWork(String email, Long volunteerWorkId) {
        Organization userOrganization = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new).getOrganization();
        Organization volunteerOrganization = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new).getOrganization();
        return !userOrganization.equals(volunteerOrganization);
    }

}

