package com.study.dahween.volunteer.service;

import com.study.dahween.common.exception.AlreadyProcessedException;
import com.study.dahween.common.exception.AuthorizationFailedException;
import com.study.dahween.organization.entity.Organization;
import com.study.dahween.organization.repository.OrganRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolunteerService {

    private final OrganRepository organRepository;
    private final VolunteerWorkRepository volunteerWorkRepository;
    private final UserVolunteerRepository userVolunteerRepository;
    private final UserRepository userRepository;
    private final Semaphore semaphore;

    public VolunteerInfoResponseDto getVolunteer(Long id) {
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new VolunteerInfoResponseDto(volunteerWork);
    }

    public List<VolunteerInfoResponseDto> getVolunteersWithinRadius(double latitude, double longitude, int radius){
        return null;
    }

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

    public List<UserInfoResponseDto> getUserListByStatusForOrganization(Long volunteerWorkId, String email, ApplyStatus status) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        Long organizationId = Optional.ofNullable(user.getOrganization())
                .map(Organization::getId)
                .orElse(null);

        if (organizationId == null) {
            log.info("소속된 기관이 없는 유저 email = {}", email);
            throw new IllegalStateException("소속된 기관이 없습니다.");
        }

        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);

        if (!organizationId.equals(volunteerWork.getOrganization().getId())) {
            log.info("해당 봉사활동과 관련된 기관 담당이 아닙니다");
            throw new IllegalStateException();
        }

        // Status 에 따라 UserList가 반환이 다름
        return userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }

    public List<UserInfoResponseDto> getUserListByStatusForAdmin(Long volunteerWorkId, ApplyStatus status) {
        return userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }

    public List<VolunteerInfoResponseDto> getAllVolunteersByOrganization(Long organizationId) {
        Organization organization = organRepository.findById(organizationId).orElseThrow(EntityNotFoundException::new);

        List<VolunteerWork> volunteerWorks = volunteerWorkRepository.getAllByOrganization(organization).orElseThrow(EntityNotFoundException::new);

        return volunteerWorks.stream().map(VolunteerInfoResponseDto::new).toList();

    }

    public List<UserInfoResponseDto> getAllUsersByVolunteerWork(Long volunteerWorkId) {
        return userVolunteerRepository.findUsersByVolunteerWorkId(volunteerWorkId).orElseThrow(EntityNotFoundException::new)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }




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

        if(equalOrganizationByUserAndVolunteerWork(requestEmail, volunteerWorkId)){
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

        if(equalOrganizationByUserAndVolunteerWork(requestEmail, volunteerWorkId)){
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

