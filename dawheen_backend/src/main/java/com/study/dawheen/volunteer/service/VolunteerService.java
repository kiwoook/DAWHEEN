package com.study.dawheen.volunteer.service;

import com.study.dawheen.common.exception.AlreadyProcessedException;
import com.study.dawheen.common.exception.AuthorizationFailedException;
import com.study.dawheen.infra.file.service.FileService;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.service.OrganSubscribeService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolunteerService {

    private static final String AUTHORIZATION_ERR_MSG = "you are not allowed to approve method";
    private final VolunteerWorkRepository volunteerWorkRepository;
    private final UserVolunteerRepository userVolunteerRepository;
    private final OrganSubscribeService organSubscribeService;
    private final FileService fileService;
    private final UserRepository userRepository;

    @Transactional
    public VolunteerInfoResponseDto create(String email, VolunteerCreateRequestDto createResponseDto, MultipartFile file, List<MultipartFile> files) throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        VolunteerWork volunteerWork = VolunteerWork.toEntity(createResponseDto);

        // 이미지 저장
        fileService.saveImgFileByVolunteerWork(file, volunteerWork);

        for (MultipartFile multipartFile : files) {
            fileService.saveImgFileByVolunteerWork(multipartFile, volunteerWork);
        }

        Organization organization = user.getOrganization();

        if (organization != null) {
            volunteerWork.updateOrganization(organization);
            organSubscribeService.sendNotify(organization.getId());
        }

        VolunteerWork savedVolunteerWork = volunteerWorkRepository.save(volunteerWork);

        return new VolunteerInfoResponseDto(savedVolunteerWork);
    }

    @Transactional
    public void delete(Long volunteerWorkId) throws EmptyResultDataAccessException, EntityNotFoundException {
        List<UserVolunteerWork> userVolunteerWorks = userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(volunteerWorkId);

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

        if (userVolunteerRepository.existsByVolunteerWorkAndEmailAndStatus(volunteerWorkId, email, List.of(ApplyStatus.APPROVED, ApplyStatus.PENDING))) {
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
    public void approve(Long volunteerWorkId, Long userId) throws IllegalAccessException {
        // 승인권한은 해당 기관담당만 가능하도록 해야함.
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);
        VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();
        String requestEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Organization organization = userRepository.findByEmail(requestEmail).orElseThrow(EntityNotFoundException::new).getOrganization();

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
    }

    @Transactional
    public void completed(Long volunteerWorkId, Long userId) throws IllegalStateException {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);
        if (userVolunteerWork.getStatus() != ApplyStatus.APPROVED) {
            throw new IllegalStateException();
        }
        userVolunteerWork.updateStatus(ApplyStatus.COMPLETED);
    }

    @Transactional
    public void cancelPending(Long volunteerWorkId, Long userId) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);
    }

    @Transactional
    public void cancelPending(Long volunteerWorkId, String email) {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndEmail(volunteerWorkId, email).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);
    }


    public void cancelApproved(Long volunteerWorkId, Long userId) {
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
    public void cancelPendingForOrganization(Long volunteerWorkId, Long userId) {
        String requestEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (equalOrganizationByUserAndVolunteerWork(requestEmail, volunteerWorkId)) {
            throw new AuthorizationFailedException(AUTHORIZATION_ERR_MSG);
        }

        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException();
        }

        userVolunteerWork.updateStatus(ApplyStatus.REJECTED);
    }

    @Transactional
    public void cancelApprovedForOrganization(Long volunteerWorkId, Long userId) {
        String requestEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (equalOrganizationByUserAndVolunteerWork(requestEmail, volunteerWorkId)) {
            throw new AuthorizationFailedException(AUTHORIZATION_ERR_MSG);
        }

        cancelApproved(volunteerWorkId, userId);
    }

    private boolean equalOrganizationByUserAndVolunteerWork(String email, Long volunteerWorkId) {
        Organization userOrganization = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new).getOrganization();
        Organization volunteerOrganization = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new).getOrganization();
        return !userOrganization.equals(volunteerOrganization);
    }

}

