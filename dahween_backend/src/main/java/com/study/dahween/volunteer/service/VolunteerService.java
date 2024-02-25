package com.study.dahween.volunteer.service;

import com.study.dahween.common.exception.AlreadyProcessedException;
import com.study.dahween.organization.entity.Organization;
import com.study.dahween.user.dto.UserInfoResponseDto;
import com.study.dahween.user.entity.User;
import com.study.dahween.user.repository.UserRepository;
import com.study.dahween.volunteer.dto.VolunteerCreateRequestDto;
import com.study.dahween.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dahween.volunteer.dto.VolunteerUpdateResponseDto;
import com.study.dahween.volunteer.entity.UserVolunteerWork;
import com.study.dahween.volunteer.entity.VolunteerWork;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import com.study.dahween.volunteer.repository.UserVolunteerRepository;
import com.study.dahween.volunteer.repository.VolunteerWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolunteerService {

    private final VolunteerWorkRepository volunteerWorkRepository;
    private final UserVolunteerRepository userVolunteerRepository;
    private final UserRepository userRepository;
    private final Semaphore semaphore;

    public VolunteerInfoResponseDto getVolunteer(Long id) {
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new VolunteerInfoResponseDto(volunteerWork);
    }

    @Transactional
    public VolunteerInfoResponseDto create(String userId, VolunteerCreateRequestDto createResponseDto) {
        User user = userRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
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

    public List<UserInfoResponseDto> getUserListByStatusForOrganization(String userId, ApplyStatus status) {
        User user = userRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        Long volunteerWorkId = Optional.ofNullable(user.getOrganization())
                .map(Organization::getId)
                .orElse(null);

        if (volunteerWorkId == null) {
            log.info("소속된 기관이 없는 유저 userId = {}", userId);
            throw new IllegalStateException("소속된 기관이 없습니다.");
        }

        // Status 에 따라 UserList가 반환이 다름
        return userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }

    public List<UserInfoResponseDto> getUserListByStatusForAdmin(Long volunteerWorkId, ApplyStatus status) {
        // Status 에 따라 UserList가 반환이 다름
        return userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }

    // TODO 특정 ORGANIZATION 에서 전체 봉사활동 확인
    public List<UserInfoResponseDto> getAllUsersListById(Long volunteerWorkId) {
        return userVolunteerRepository.findUsersByVolunteerWorkId(volunteerWorkId).orElseThrow(EntityNotFoundException::new)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }

    @Transactional
    public void deleteUserVolunteerWork(Long id) throws EmptyResultDataAccessException, EntityNotFoundException {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() == ApplyStatus.APPROVED) {
            User user = userVolunteerWork.getUser();
            VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();

            user.leaveVolunteerWork(userVolunteerWork);
            volunteerWork.leaveUser(userVolunteerWork);
        }

        userVolunteerRepository.delete(userVolunteerWork);
    }

    @Transactional
    public void apply(Long volunteerWorkId, String userId) {
        // 수락중이거나 대기 중이라면 신청이 안되도록 해야함.

        if (userVolunteerRepository.existsByVolunteerWorkAndUserAndStatus(volunteerWorkId, userId, List.of(ApplyStatus.APPROVED, ApplyStatus.PENDING))) {
            log.info("이미 신청한 아이디 userId = {}, volunteerWorkId = {}", userId, volunteerWorkId);
            throw new AlreadyProcessedException();
        }

        User user = userRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);

        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        userVolunteerRepository.save(userVolunteerWork);
    }

    public void approve(Long volunteerWorkId, String userId) throws InterruptedException {

        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);
        VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();

        if (volunteerWork.getAppliedParticipants().get() == volunteerWork.getMaxParticipants()) {
            throw new IllegalStateException();
        }

        try {
            semaphore.acquire();
            // 동기화가 필요한 로직에 대해서만 넣자
            if (userVolunteerWork.getUser().getUserId().equals(userId) && userVolunteerWork.getStatus().equals(ApplyStatus.PENDING)) {
                User user = userVolunteerWork.getUser();
                user.attendVolunteerWork(userVolunteerWork);
                volunteerWork.attendUser(userVolunteerWork);
                volunteerWork.increaseParticipants();
            } else {
                throw new IllegalStateException();
            }

        } finally {
            semaphore.release();
        }
    }

    @Transactional
    public void cancelPending(Long volunteerWorkId, String userId) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);
    }

    @Transactional
    public void cancelApproved(Long volunteerWorkId, String userId) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.APPROVED) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);

        User user = userVolunteerWork.getUser();
        VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();

        user.leaveVolunteerWork(userVolunteerWork);
        volunteerWork.leaveUser(userVolunteerWork);
    }


}
