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

    public List<UserInfoResponseDto> getAllUsersByVolunteerWork(Long volunteerWorkId) {
        return userVolunteerRepository.findUsersByVolunteerWorkId(volunteerWorkId)
                .stream()
                .map(UserInfoResponseDto::new)
                .toList();
    }
}
