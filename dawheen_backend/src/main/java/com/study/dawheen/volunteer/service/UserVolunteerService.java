package com.study.dawheen.volunteer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.study.dawheen.common.exception.AlreadyProcessedException;
import com.study.dawheen.common.exception.AuthorizationFailedException;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import com.study.dawheen.volunteer.service.impl.VolunteerRankingServiceV2;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVolunteerService {

    private static final String AUTHORIZATION_ERR_MSG = "you are not allowed to approve method";

    private final UserVolunteerRepository userVolunteerRepository;
    private final UserRepository userRepository;
    private final VolunteerWorkRepository volunteerWorkRepository;
    private final VolunteerRankingServiceV2 volunteerRankingService;

    // 대기중인 유저를 확인해 신청할지 안할지 정할 수 있음.
    // 소속된 기관의 유저 리스트 반환
    @Retryable(retryFor = {OptimisticLockException.class}, backoff = @Backoff(delay = 500))
    @Transactional
    public void apply(Long volunteerWorkId, String email) {

        log.info("apply 실행 volunteerWorkId = {}, email = {}", volunteerWorkId, email);

        if (userVolunteerRepository.existsByVolunteerWorkAndEmailAndStatus(volunteerWorkId, email, List.of(ApplyStatus.APPROVED, ApplyStatus.PENDING))) {
            log.info("이미 신청한 아이디 email = {}, volunteerWorkId = {}", email, volunteerWorkId);
            throw new AlreadyProcessedException();
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(() -> new EntityNotFoundException("VolunteerWork not found for id: " + volunteerWorkId));

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(volunteerWork.getRecruitEndDateTime())) {
            throw new IllegalStateException("Recruitment period has ended.");
        }

        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        userVolunteerRepository.save(userVolunteerWork);
    }

    @Transactional
    public void unregisterUsersFromVolunteerWork(Long volunteerWorkId) {
        List<UserVolunteerWork> userVolunteerWorks = userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(volunteerWorkId).orElseThrow(() -> new EntityNotFoundException("VolunteerWork with ID :" + volunteerWorkId + " not found"));

        for (UserVolunteerWork userVolunteerWork : userVolunteerWorks) {
            User user = userVolunteerWork.getUser();
            user.leaveVolunteerWork(userVolunteerWork);
        }

        userRepository.saveAll(userVolunteerWorks.stream().map(UserVolunteerWork::getUser).toList());
    }

    @Transactional
    public void deleteApprovedUserVolunteerWork(Long userVolunteerWorkId) throws EmptyResultDataAccessException, EntityNotFoundException {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findById(userVolunteerWorkId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() == ApplyStatus.APPROVED) {
            User user = userVolunteerWork.getUser();
            VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();

            user.leaveVolunteerWork(userVolunteerWork);
            volunteerWork.leaveUser(userVolunteerWork);
        }

        userVolunteerRepository.delete(userVolunteerWork);
    }


    public Page<VolunteerInfoResponseDto> getParticipateVolunteerWorkByUser(String email, Pageable pageable) {
        return userVolunteerRepository.findVolunteerWorkByEmailAndStatus(email, ApplyStatus.COMPLETED, pageable);

    }

    @Retryable(retryFor = {EntityNotFoundException.class}, backoff = @Backoff(delay = 3000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void approve(String email, Long volunteerWorkId, Long userId) throws IllegalAccessException {
        // 승인권한은 해당 기관담당만 가능하도록 해야함.

        log.info("approve 호출 email = {}, volunteerWork = {}, userId = {}", email, volunteerWorkId, userId);
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException("대기 상태가 아닙니다. userVolunteerWorkId =" + userVolunteerWork.getId());
        }

        VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();
        Organization organization = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new).getOrganization();

        if (!volunteerWork.getOrganization().equals(organization)) {
            throw new AuthorizationFailedException(AUTHORIZATION_ERR_MSG);
        }

        if (volunteerWork.getAppliedParticipants().get() >= volunteerWork.getMaxParticipants()) {
            throw new IllegalAccessException();
        }

        User user = userVolunteerWork.getUser();
        user.attendVolunteerWork(userVolunteerWork);
        volunteerWork.attendUser(userVolunteerWork);
        volunteerWork.increaseParticipants();
        userVolunteerWork.updateStatus(ApplyStatus.APPROVED);
    }

    @Transactional
    public void completed(Long volunteerWorkId, Long userId) throws IllegalStateException, JsonProcessingException {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.APPROVED) throw new IllegalStateException();

        userVolunteerWork.updateStatus(ApplyStatus.COMPLETED);
        volunteerRankingService.addVolunteerUser(userVolunteerWork.getUser().getEmail());
    }

    @Transactional
    public void cancelPending(Long volunteerWorkId, Long userId) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        cancelPending(userVolunteerWork);
    }

    @Transactional
    public void cancelPending(Long volunteerWorkId, String email) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndEmail(volunteerWorkId, email).orElseThrow(EntityNotFoundException::new);

        cancelPending(userVolunteerWork);
    }

    private void cancelPending(UserVolunteerWork userVolunteerWork) {
        if (userVolunteerWork.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);
    }

    public void cancelApproved(Long volunteerWorkId, Long userId) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        cancel(userVolunteerWork);
    }

    @Transactional
    public void cancelApproved(Long volunteerWorkId, String email) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndEmail(volunteerWorkId, email).orElseThrow(EntityNotFoundException::new);

        cancel(userVolunteerWork);
    }

    @Transactional
    public void cancelPendingForOrganization(String email, Long volunteerWorkId, Long userId) {

        if (equalOrganizationByUserAndVolunteerWork(email, volunteerWorkId)) {
            throw new AuthorizationFailedException(AUTHORIZATION_ERR_MSG);
        }

        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);
    }

    @Transactional
    public void cancelApprovedForOrganization(String email, Long volunteerWorkId, Long userId) {

        if (equalOrganizationByUserAndVolunteerWork(email, volunteerWorkId)) {
            throw new AuthorizationFailedException(AUTHORIZATION_ERR_MSG);
        }

        cancelApproved(volunteerWorkId, userId);
    }

    private boolean equalOrganizationByUserAndVolunteerWork(String email, Long volunteerWorkId) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);
        Organization userOrganization = user.getOrganization();
        Organization volunteerOrganization = volunteerWork.getOrganization();

        if (userOrganization == null || volunteerOrganization == null) {
            throw new AuthorizationFailedException(AUTHORIZATION_ERR_MSG);
        }

        return !userOrganization.equals(volunteerOrganization);
    }

    private void cancel(UserVolunteerWork userVolunteerWork) {
        if (userVolunteerWork.getStatus() != ApplyStatus.APPROVED) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);

        User user = userVolunteerWork.getUser();
        VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();

        user.leaveVolunteerWork(userVolunteerWork);
        volunteerWork.leaveUser(userVolunteerWork);

        volunteerWork.decreaseParticipants();
    }
}
