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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VolunteerUserQueryServiceTest {



    @Mock
    private UserRepository userRepository;

    @Mock
    private VolunteerWorkRepository volunteerWorkRepository;

    @Mock
    private UserVolunteerRepository userVolunteerRepository;

    @InjectMocks
    private VolunteerUserQueryService volunteerUserQueryService;

    @MockBean
    private VolunteerRankingService volunteerRankingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("정상적인 상황 - 유효한 봉사 활동 ID, 이메일, 상태 제공")
    void getUserListByStatusForOrganization_Success() {
        // given
        String email = "test@example.com";
        Long volunteerWorkId = 1L;
        ApplyStatus status = ApplyStatus.PENDING;

        User mockUser = mock(User.class);
        Organization mockOrganization = mock(Organization.class);
        VolunteerWork mockVolunteerWork = mock(VolunteerWork.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(mockUser.getOrganization()).thenReturn(mockOrganization);
        when(mockOrganization.getId()).thenReturn(1L);

        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.of(mockVolunteerWork));
        when(mockVolunteerWork.getOrganization()).thenReturn(mockOrganization);

        when(userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status)).thenReturn(Optional.of(List.of(mockUser)));

        // when
        List<UserInfoResponseDto> result = volunteerUserQueryService.getUserListByStatusForOrganization(volunteerWorkId, email, status);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByEmail(email);
        verify(volunteerWorkRepository).findById(volunteerWorkId);
        verify(userVolunteerRepository).findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status);
    }

    @Test
    @DisplayName("예외 상황 - 존재하지 않는 이메일 제공")
    void getUserListByStatusForOrganization_EmailNotFound() {
        // given
        String email = "nonexistent@example.com";
        Long volunteerWorkId = 1L;
        ApplyStatus status = ApplyStatus.PENDING;

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            volunteerUserQueryService.getUserListByStatusForOrganization(volunteerWorkId, email, status);
        });
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("예외 상황 - 소속된 조직이 없는 경우")
    void getUserListByStatusForOrganization_NoOrganization() {
        // given
        String email = "test@example.com";
        Long volunteerWorkId = 1L;
        ApplyStatus status = ApplyStatus.PENDING;

        User mockUser = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(mockUser.getOrganization()).thenReturn(null);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            volunteerUserQueryService.getUserListByStatusForOrganization(volunteerWorkId, email, status);
        });

        assertEquals("해당 유저는 소속된 기관이 없습니다: " + email, exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("예외 상황 - 봉사 활동과 관련된 조직이 아닌 경우")
    void getUserListByStatusForOrganization_NotRelatedOrganization() {
        // given
        String email = "test@example.com";
        Long volunteerWorkId = 1L;
        ApplyStatus status = ApplyStatus.PENDING;

        User mockUser = mock(User.class);
        Organization userOrganization = mock(Organization.class);
        Organization otherOrganization = mock(Organization.class);
        VolunteerWork mockVolunteerWork = mock(VolunteerWork.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(mockUser.getOrganization()).thenReturn(userOrganization);
        when(userOrganization.getId()).thenReturn(1L);

        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.of(mockVolunteerWork));
        when(mockVolunteerWork.getOrganization()).thenReturn(otherOrganization);
        when(otherOrganization.getId()).thenReturn(2L);

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            volunteerUserQueryService.getUserListByStatusForOrganization(volunteerWorkId, email, status);
        });

        verify(userRepository).findByEmail(email);
        verify(volunteerWorkRepository).findById(volunteerWorkId);
    }

    @Test
    @DisplayName("예외 상황 - 존재하지 않는 봉사 활동 ID 제공")
    void getUserListByStatusForOrganization_VolunteerWorkNotFound() {
        // given
        String email = "test@example.com";
        Long volunteerWorkId = 999L;
        ApplyStatus status = ApplyStatus.PENDING;

        User mockUser = mock(User.class);
        Organization mockOrganization = mock(Organization.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(mockUser.getOrganization()).thenReturn(mockOrganization);
        when(mockOrganization.getId()).thenReturn(1L);

        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            volunteerUserQueryService.getUserListByStatusForOrganization(volunteerWorkId, email, status);
        });

        verify(userRepository).findByEmail(email);
        verify(volunteerWorkRepository).findById(volunteerWorkId);
    }

    @Test
    @DisplayName("정상적인 상황 - 유효한 봉사 활동 ID와 상태 제공")
    void getUserListByStatusForAdmin_Success() {
        // given
        Long volunteerWorkId = 1L;
        ApplyStatus status = ApplyStatus.APPROVED;
        User mockUser = mock(User.class);

        when(userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status)).thenReturn(Optional.of(List.of(mockUser)));

        // when
        List<UserInfoResponseDto> result = volunteerUserQueryService.getUserListByStatusForAdmin(volunteerWorkId, status);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userVolunteerRepository).findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status);
    }

    @Test
    @DisplayName("예외 상황 - 존재하지 않는 봉사 활동 ID 제공")
    void getUserListByStatusForAdmin_VolunteerWorkNotFound() {
        // given
        Long volunteerWorkId = 999L;
        ApplyStatus status = ApplyStatus.APPROVED;

        when(userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            volunteerUserQueryService.getUserListByStatusForAdmin(volunteerWorkId, status);
        });

        verify(userVolunteerRepository).findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, status);
    }
}
