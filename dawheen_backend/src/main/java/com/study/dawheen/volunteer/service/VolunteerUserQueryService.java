package com.study.dawheen.volunteer.service;

import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolunteerUserQueryService {

    private final UserRepository userRepository;
    private final UserVolunteerRepository userVolunteerRepository;
    private final VolunteerWorkRepository volunteerWorkRepository;

    public List<UserInfoResponseDto> getUserListByStatusForOrganization(Long volunteerWorkId, String email, ApplyStatus status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다: " + email));

        Organization userOrganization = Optional.ofNullable(user.getOrganization())
                .orElseThrow(() -> new IllegalStateException("해당 유저는 소속된 기관이 없습니다: " + email));

        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId)
                .orElseThrow(() -> new EntityNotFoundException("해당 봉사활동을 찾을 수 없습니다: " + volunteerWorkId));

        Organization volunteerOrganization = Optional.ofNullable(volunteerWork.getOrganization())
                .orElseThrow(() -> new IllegalStateException("해당 봉사활동에 소속된 기관이 없습니다: " + volunteerWorkId));

        if (!userOrganization.equals(volunteerOrganization)) {
            log.info("해당 봉사활동과 관련된 기관 담당이 아닙니다. email: {}, volunteerWorkId: {}", email, volunteerWorkId);
            throw new IllegalStateException("해당 봉사활동에 대한 권한이 없습니다.");
        }

        // Status 에 따라 UserList가 반환이 다름
        return userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status).orElseThrow(EntityNotFoundException::new)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }

    public List<UserInfoResponseDto> getUserListByStatusForAdmin(Long volunteerWorkId, ApplyStatus status) {
        return userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status)
                .orElseThrow(EntityNotFoundException::new)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }

    public List<UserInfoResponseDto> getAllUsersByVolunteerWork(Long volunteerWorkId) {
        return userVolunteerRepository.findUsersByVolunteerWorkId(volunteerWorkId)
                .orElseThrow(EntityNotFoundException::new)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }
}
