package com.study.dawheen.volunteer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.study.dawheen.common.exception.AlreadyProcessedException;
import com.study.dawheen.common.exception.AuthorizationFailedException;
import com.study.dawheen.config.TestSecurityConfig;
import com.study.dawheen.infra.file.service.FileService;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.service.OrganSubscribeService;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerCreateRequestDto;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.dto.VolunteerUpdateRequestDto;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import com.study.dawheen.volunteer.service.impl.VolunteerRankingServiceV2;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class VolunteerServiceTest {

    @Mock
    Organization organization;
    @Mock
    private VolunteerWorkRepository volunteerWorkRepository;
    @Mock
    private UserVolunteerRepository userVolunteerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrganSubscribeService organSubscribeService;
    @Mock
    private FileService fileService;
    @Mock
    private MultipartFile file;
    @Mock
    private VolunteerCreateRequestDto createResponseDto;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private VolunteerService volunteerService;
    @Mock
    private VolunteerRankingServiceV2 volunteerRankingService;


    private VolunteerWork volunteerWork;
    private User user;
    private String userEmail;

    @BeforeEach
    void setUp() {
        // Given

        when(passwordEncoder.encode(Mockito.anyString())).thenAnswer(invocation -> {
            String rawPassword = invocation.getArgument(0);
            String encodedPassword = "encoded_" + rawPassword;
            return String.format("%1$-" + 60 + "s", encodedPassword).replace(' ', 'x');
        });

        volunteerWork = VolunteerWork.builder().organization(organization).title("Sample Volunteer Work").content("This is a sample content.").serviceStartDatetime(LocalDateTime.of(2024, 1, 1, 9, 0)).serviceEndDatetime(LocalDateTime.of(2024, 12, 31, 17, 0)).serviceDays(Set.of(LocalDate.now().getDayOfWeek())).targetAudiences(Set.of(TargetAudience.ANIMAL)).volunteerTypes(Set.of(VolunteerType.ADULT)).recruitStartDateTime(LocalDateTime.now()).recruitEndDateTime(LocalDateTime.now().plusMonths(1)).maxParticipants(100).build();

        userEmail = "test@example.com";

        user = User.builder().email(userEmail).password(passwordEncoder.encode("1234")).name("user").build();

        user.grantOrganization(organization);
    }

    @Test
    @DisplayName("봉사 생성 성공")
    void create_Success() throws IOException {
        // When
        when(organization.getId()).thenReturn(1L);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(volunteerWorkRepository.save(any(VolunteerWork.class))).thenReturn(volunteerWork);

        VolunteerInfoResponseDto response = volunteerService.create(userEmail, createResponseDto, file, List.of(file));


        // Then
        verify(fileService, times(2)).saveImgFileByVolunteerWork(any(MultipartFile.class), any(VolunteerWork.class));
        verify(organSubscribeService).sendNotify(anyLong());
        assertNotNull(response);
    }

    @Test
    @DisplayName("봉사 생성 성공: 파일 null")
    void create_Success_FileNull() throws IOException {
        when(organization.getId()).thenReturn(1L);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(volunteerWorkRepository.save(any(VolunteerWork.class))).thenReturn(volunteerWork);

        // When
        VolunteerInfoResponseDto response = volunteerService.create(userEmail, createResponseDto, null, null);

        // Then
        verify(fileService, times(0)).saveImgFileByVolunteerWork(any(MultipartFile.class), any(VolunteerWork.class)); // No file operations should be invoked
        verify(organSubscribeService).sendNotify(anyLong());
        assertNotNull(response); // Ensure the response is not null
    }

    @Test
    @DisplayName("봉사 생성 실패: 이메일 미제공")
    void create_UserNotFoundException() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        List<MultipartFile> files = List.of(file);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> volunteerService.create(nonExistentEmail, createResponseDto, file, files));
    }

    @Test
    @DisplayName("봉사 생성 실패: 파일 저장 중 IOException 발생")
    void create_IOException() throws IOException {
        // Given
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        doThrow(new IOException("File I/O error")).when(fileService).saveImgFileByVolunteerWork(any(MultipartFile.class), any(VolunteerWork.class));

        // When & Then
        assertThrows(IOException.class, () -> volunteerService.create(userEmail, createResponseDto, file, List.of(file)));
    }

    @Test
    @DisplayName("봉사 활동 삭제 성공")
    void delete_Success() {
        // Given
        Long volunteerWorkId = 1L;


        VolunteerWork mockVolunteerWork = mock(VolunteerWork.class);
        UserVolunteerWork mockUserVolunteerWork = mock(UserVolunteerWork.class);
        User mockUser = mock(User.class);

        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(volunteerWorkId)).thenReturn(Optional.of(List.of(mockUserVolunteerWork)));
        when(mockUserVolunteerWork.getUser()).thenReturn(mockUser);
        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.of(mockVolunteerWork));

        // When
        volunteerService.delete(volunteerWorkId);

        // Then
        verify(mockUser).leaveVolunteerWork(mockUserVolunteerWork);
        verify(userVolunteerRepository).findAllByVolunteerWorkIdWithFetch(volunteerWorkId);
        verify(volunteerWorkRepository).delete(mockVolunteerWork);
    }

    @Test
    @DisplayName("봉사 활동 삭제 실패: 봉사 활동 ID 미존재")
    void delete_Fail_NonExistentVolunteerWork() {
        // Given
        Long nonExistentVolunteerWorkId = 999L;

        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(nonExistentVolunteerWorkId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> volunteerService.delete(nonExistentVolunteerWorkId));

        verify(userVolunteerRepository).findAllByVolunteerWorkIdWithFetch(nonExistentVolunteerWorkId);
    }

    @Test
    @DisplayName("봉사 활동 삭제 실패: UserVolunteerWork 미존재")
    void delete_Fail_NoUserVolunteerWork() {
        // Given
        Long volunteerWorkId = 1L;

        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(volunteerWorkId)).thenReturn(Optional.empty());


        // When & Then
        assertThrows(EntityNotFoundException.class, () -> volunteerService.delete(volunteerWorkId));

        verify(userVolunteerRepository).findAllByVolunteerWorkIdWithFetch(volunteerWorkId);
        verify(volunteerWorkRepository, never()).delete(any(VolunteerWork.class));
    }

    @Test
    @DisplayName("봉사 활동 삭제 실패: UserVolunteerWork 존재 VolunteerWork 미존재")
    void delete_Failure_UserVolunteerWorkExists_But_VolunteerWorkNotFound() {
        // Given
        Long volunteerWorkId = 1L;

        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        List<UserVolunteerWork> userVolunteerWorks = List.of(userVolunteerWork);


        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(anyLong())).thenReturn(Optional.of(userVolunteerWorks));

        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> volunteerService.delete(volunteerWorkId));

        verify(userVolunteerRepository, times(1)).findAllByVolunteerWorkIdWithFetch(volunteerWorkId);
        verify(volunteerWorkRepository, times(1)).findById(volunteerWorkId);
        verify(volunteerWorkRepository, never()).delete(any(VolunteerWork.class));  // 삭제 시도하지 않았는지 확인
    }

    @Test
    @DisplayName("봉사 활동 업데이트 성공 테스트")
    @Transactional
    void update_Success() {
        Long volunteerWorkId = 1L;

        // Given
        VolunteerUpdateRequestDto updateRequestDto = new VolunteerUpdateRequestDto("New Title", "New Content", LocalDateTime.of(2023, 1, 1, 9, 0), LocalDateTime.of(2023, 12, 31, 17, 0), new HashSet<>(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)), new HashSet<>(Set.of(TargetAudience.ANIMAL)), new HashSet<>(Set.of(VolunteerType.ADULT)), LocalDateTime.now(), LocalDateTime.now().plusDays(30), 10, 37.7749, -122.4194);

        // When
        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.of(volunteerWork));

        VolunteerInfoResponseDto response = volunteerService.update(volunteerWorkId, updateRequestDto);

        // Then
        assertNotNull(response);
        assertEquals(volunteerWork.getTitle(), response.getTitle());
        assertEquals(volunteerWork.getContent(), response.getContent());
        // 나머지 필드 검증
        verify(volunteerWorkRepository).findById(volunteerWorkId);
    }

    @Test
    @DisplayName("봉사 활동 업데이트 성공: null 포함")
    @Transactional
    void update_Success_with_null() {
        Long volunteerWorkId = 1L;

        // Given
        VolunteerUpdateRequestDto updateRequestDto = new VolunteerUpdateRequestDto(null, null, LocalDateTime.of(2023, 1, 1, 9, 0), LocalDateTime.of(2023, 12, 31, 17, 0), new HashSet<>(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)), new HashSet<>(Set.of(TargetAudience.ANIMAL)), new HashSet<>(Set.of(VolunteerType.ADULT)), LocalDateTime.now(), LocalDateTime.now().plusDays(30), 10, 37.7749, -122.4194);

        // When
        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.of(volunteerWork));

        VolunteerInfoResponseDto response = volunteerService.update(volunteerWorkId, updateRequestDto);

        // Then
        assertNotNull(response);
        assertEquals(volunteerWork.getTitle(), response.getTitle());
        assertEquals(volunteerWork.getContent(), response.getContent());
        // 나머지 필드 검증
        verify(volunteerWorkRepository).findById(volunteerWorkId);
    }

    @Test
    @DisplayName("봉사 활동 업데이트 실패: 존재하지 않는 봉사 활동 ID")
    void update_Failure_EntityNotFound() {
        Long volunteerWorkId = 1L;

        // Given
        VolunteerUpdateRequestDto updateRequestDto = new VolunteerUpdateRequestDto("New Title", "New Content", LocalDateTime.of(2023, 1, 1, 9, 0), LocalDateTime.of(2023, 12, 31, 17, 0), new HashSet<>(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)), new HashSet<>(Set.of(TargetAudience.ANIMAL)), new HashSet<>(Set.of(VolunteerType.ADULT)), LocalDateTime.now(), LocalDateTime.now().plusDays(30), 10, 37.7749, -122.4194);


        // When
        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> volunteerService.update(volunteerWorkId, updateRequestDto));

        verify(volunteerWorkRepository).findById(volunteerWorkId);
    }

    @Test
    @DisplayName("봉사 활동 신청 성공 테스트")
    void apply_Success() {
        Long volunteerWorkId = 1L;
        String email = "user@example.com";

        volunteerWork = VolunteerWork.builder().organization(organization).title("Sample Volunteer Work").content("This is a sample content.").serviceStartDatetime(LocalDateTime.of(2024, 1, 1, 9, 0)).serviceEndDatetime(LocalDateTime.now().plusDays(1)).serviceDays(Set.of(LocalDate.now().getDayOfWeek())).targetAudiences(Set.of(TargetAudience.ANIMAL)).volunteerTypes(Set.of(VolunteerType.ADULT)).recruitStartDateTime(LocalDateTime.now()).recruitEndDateTime(LocalDateTime.now().plusMonths(1)).maxParticipants(100).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.of(volunteerWork));
        when(userVolunteerRepository.existsByVolunteerWorkAndEmailAndStatus(volunteerWorkId, email, List.of(ApplyStatus.APPROVED, ApplyStatus.PENDING))).thenReturn(false);

        // When
        volunteerService.apply(volunteerWorkId, email);

        // Then
        verify(userVolunteerRepository).save(any(UserVolunteerWork.class));
    }

    @Test
    @DisplayName("봉사 활동 신청 실패: 이미 신청한 사용자")
    void apply_Failure_AlreadyProcessed() {
        Long volunteerWorkId = 1L;
        String email = "user@example.com";

        when(userVolunteerRepository.existsByVolunteerWorkAndEmailAndStatus(volunteerWorkId, email, List.of(ApplyStatus.APPROVED, ApplyStatus.PENDING))).thenReturn(true);

        // When & Then
        assertThrows(AlreadyProcessedException.class, () -> volunteerService.apply(volunteerWorkId, email));

        verify(userVolunteerRepository, never()).save(any(UserVolunteerWork.class));
    }

    @Test
    @DisplayName("봉사 활동 신청 실패: 신청 기간이 지나간 봉사 활동")
    void apply_Failure_RecruitEndDateTimePassed() {
        Long volunteerWorkId = 1L;
        String email = "user@example.com";
        volunteerWork = VolunteerWork.builder().organization(organization).title("Sample Volunteer Work").content("This is a sample content.").serviceStartDatetime(LocalDateTime.of(2024, 1, 1, 9, 0)).serviceEndDatetime(LocalDateTime.now().plusDays(1)).serviceDays(Set.of(LocalDate.now().getDayOfWeek())).targetAudiences(Set.of(TargetAudience.ANIMAL)).volunteerTypes(Set.of(VolunteerType.ADULT)).recruitStartDateTime(LocalDateTime.now()).recruitEndDateTime(LocalDateTime.now().minusDays(1)).maxParticipants(100).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.of(volunteerWork));
        when(userVolunteerRepository.existsByVolunteerWorkAndEmailAndStatus(volunteerWorkId, email, List.of(ApplyStatus.APPROVED, ApplyStatus.PENDING))).thenReturn(false);

        // When & Then
        assertThrows(IllegalStateException.class, () -> volunteerService.apply(volunteerWorkId, email));

        verify(userVolunteerRepository, never()).save(any(UserVolunteerWork.class));
    }

    @Test
    @DisplayName("봉사 활동 승인 성공 테스트")
    void approve_Success() throws IllegalAccessException {
        Long volunteerWorkId = 1L;
        Long userId = 2L;

        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.of(userVolunteerWork));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        volunteerService.approve(userEmail, volunteerWorkId, userId);

        assertEquals(1, volunteerWork.getAppliedParticipants().get());
    }


    @Test
    @DisplayName("봉사 활동 승인 실패: 승인 권한 없음")
    void approve_Failure_AuthorizationFailed() {
        Long volunteerWorkId = 1L;
        Long userId = 2L;

        // 유저 기관 연결 삭제
        user.revokeOrganization();
        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.of(userVolunteerWork));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        assertThrows(AuthorizationFailedException.class, () -> volunteerService.approve(userEmail, volunteerWorkId, userId));

        verify(userVolunteerRepository, never()).save(any(UserVolunteerWork.class));
    }

    @Test
    @DisplayName("봉사 활동 승인 실패: 최대 참가 인원 초과")
    void approve_Failure_MaxParticipantsExceeded() {
        Long volunteerWorkId = 1L;
        Long userId = 2L;

        volunteerWork = VolunteerWork.builder().organization(organization).title("Sample Volunteer Work").content("This is a sample content.").serviceStartDatetime(LocalDateTime.of(2024, 1, 1, 9, 0)).serviceEndDatetime(LocalDateTime.now().plusDays(1)).serviceDays(Set.of(LocalDate.now().getDayOfWeek())).targetAudiences(Set.of(TargetAudience.ANIMAL)).volunteerTypes(Set.of(VolunteerType.ADULT)).recruitStartDateTime(LocalDateTime.now()).recruitEndDateTime(LocalDateTime.now().minusDays(1)).maxParticipants(0).build();

        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.of(userVolunteerWork));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        assertThrows(IllegalAccessException.class, () -> volunteerService.approve(userEmail, volunteerWorkId, userId));

        verify(userVolunteerRepository, never()).save(any(UserVolunteerWork.class));
    }

    @Test
    @DisplayName("봉사 활동 완료 성공")
    void completed_Success() throws JsonProcessingException, InterruptedException {
        // Given
        Long volunteerWorkId = 1L;
        Long userId = 1L;
        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);
        userVolunteerWork.updateStatus(ApplyStatus.APPROVED);

        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.of(userVolunteerWork));

        doNothing().when(volunteerRankingService).addVolunteerUser(anyString());

        // When
        volunteerService.completed(volunteerWorkId, userId);

        // Then
        assertEquals(ApplyStatus.COMPLETED, userVolunteerWork.getStatus());
    }

    @Test
    @DisplayName("봉사 활동 완료 실패: Approved 아님")
    void completed_failed_not_approved() {
        // Given
        Long volunteerWorkId = 1L;
        Long userId = 1L;
        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.of(userVolunteerWork));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            volunteerService.completed(volunteerWorkId, userId);
        });
    }

    @Test
    @DisplayName("봉사 활동 완료 실패: user || volunteer 미존재")
    void completed_failed_not_found() {
        // Given
        Long volunteerWorkId = 1L;
        Long userId = 1L;

        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            volunteerService.completed(volunteerWorkId, userId);
        });
    }

    @Test
    @DisplayName("봉사 활동 대기 취소 성공")
    void cancelWhenPending() {
        // Given
        Long volunteerWorkId = 1L;
        Long userId = 1L;
        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        // Mock behavior
        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.of(userVolunteerWork));

        // When
        volunteerService.cancelPending(volunteerWorkId, userId);

        // Then
        assertEquals(ApplyStatus.REJECTED, userVolunteerWork.getStatus());
        verify(userVolunteerRepository, times(1)).findByVolunteerWorkIdAndUserId(volunteerWorkId, userId);
    }

    @Test
    @DisplayName("봉사 활동 대기 취소 실패 : 대기 상태 아님")
    void shouldThrowIllegalStateExceptionWhenStatusNotPending() {
        // Given
        Long volunteerWorkId = 1L;
        Long userId = 1L;
        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);
        userVolunteerWork.updateStatus(ApplyStatus.APPROVED);

        // Mock behavior
        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.of(userVolunteerWork));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            volunteerService.cancelPending(volunteerWorkId, userId);
        });

        verify(userVolunteerRepository, times(1)).findByVolunteerWorkIdAndUserId(volunteerWorkId, userId);
    }

    @Test
    @DisplayName("봉사 활동 대기 취소 실패 : volunteer || user not found")
    void shouldThrowEntityNotFoundExceptionWhenNotFound() {
        // Given
        Long volunteerWorkId = 1L;
        Long userId = 1L;

        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            volunteerService.cancelPending(volunteerWorkId, userId);
        });

        verify(userVolunteerRepository, times(1)).findByVolunteerWorkIdAndUserId(volunteerWorkId, userId);
    }

    @Test
    @DisplayName("기관이 대기 상태 취소 성공")
    void shouldUpdateStatusToRejectedWhenPending() {
        // Given
        // 해당 유저는 기관과 연결되어 있음
        String email = "admin@example.com";
        Long volunteerWorkId = 1L;
        Long userId = 1L;
        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        when(userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId)).thenReturn(Optional.of(userVolunteerWork));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.of(volunteerWork));

        // When
        volunteerService.cancelPendingForOrganization(email, volunteerWorkId, userId);

        // Then
        assertEquals(ApplyStatus.REJECTED, userVolunteerWork.getStatus());
        verify(userVolunteerRepository, times(1)).findByVolunteerWorkIdAndUserId(volunteerWorkId, userId);
    }

    @Test
    @DisplayName("기관이 대기 상태 취소 실패: 권한 없음")
    void shouldThrowAuthorizationFailedExceptionWhenUnauthorized() {
        // Given
        // 해당 유저는 기관과 연결되어 있지 않음
        user = User.builder().email(userEmail).password(passwordEncoder.encode("1234")).name("user").build();

        Long volunteerWorkId = 1L;
        Long userId = 1L;

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(volunteerWorkRepository.findById(volunteerWorkId)).thenReturn(Optional.of(volunteerWork));
        // When & Then
        assertThrows(AuthorizationFailedException.class, () -> {
            volunteerService.cancelPendingForOrganization(userEmail, volunteerWorkId, userId);
        });

        verify(userVolunteerRepository, times(0)).findByVolunteerWorkIdAndUserId(volunteerWorkId, userId);
    }


}



