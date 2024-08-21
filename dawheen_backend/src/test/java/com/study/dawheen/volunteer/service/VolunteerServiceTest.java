package com.study.dawheen.volunteer.service;

import com.study.dawheen.config.TestSecurityConfig;
import com.study.dawheen.infra.file.service.FileService;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.service.OrganSubscribeService;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerCreateRequestDto;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        volunteerWork = VolunteerWork.builder()
                .organization(organization)
                .title("Sample Volunteer Work")
                .content("This is a sample content.")
                .serviceStartDate(LocalDate.of(2024, 1, 1))
                .serviceEndDate(LocalDate.of(2024, 12, 31))
                .serviceStartTime(LocalTime.of(9, 0))
                .serviceEndTime(LocalTime.of(17, 0))
                .serviceDays(Set.of(LocalDate.now().getDayOfWeek()))
                .targetAudiences(Set.of(TargetAudience.ANIMAL))
                .volunteerTypes(Set.of(VolunteerType.ADULT))
                .recruitStartDateTime(LocalDateTime.now())
                .recruitEndDateTime(LocalDateTime.now().plusMonths(1))
                .maxParticipants(100)
                .build();

        userEmail = "test@example.com";

        user = User.builder()
                .email(userEmail)
                .password(passwordEncoder.encode("1234"))
                .name("user")
                .build();

        user.grantOrganization(organization);
    }

    @Test
    @DisplayName("봉사 생성 성공 테스트")
    void create_Success() throws IOException {
        // When
        when(organization.getId()).thenReturn(1L);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(volunteerWorkRepository.save(any(VolunteerWork.class))).thenReturn(volunteerWork);

        VolunteerInfoResponseDto response = volunteerService.create(userEmail, createResponseDto, file, List.of(file));


        // Then
        verify(fileService, times(2))
                .saveImgFileByVolunteerWork(any(MultipartFile.class), any(VolunteerWork.class));
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
        verify(fileService, times(0))
                .saveImgFileByVolunteerWork(any(MultipartFile.class), any(VolunteerWork.class)); // No file operations should be invoked
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
        assertThrows(EntityNotFoundException.class, () -> {
            volunteerService.create(nonExistentEmail, createResponseDto, file, files);
        });
    }

    @Test
    @DisplayName("봉사 생성 실패 케이스 : 파일 저장 중 IOException")
    void create_IOException() throws IOException {
        // Given
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        doThrow(new IOException("File I/O error"))
                .when(fileService)
                .saveImgFileByVolunteerWork(any(MultipartFile.class), any(VolunteerWork.class));

        // When & Then
        assertThrows(IOException.class, () -> {
            volunteerService.create(userEmail, createResponseDto, file, List.of(file));
        });
    }

    @Test
    @DisplayName("봉사 활동 삭제 성공 케이스")
    void delete_Success() {
        // Given
        Long volunteerWorkId = 1L;


        VolunteerWork mockVolunteerWork = mock(VolunteerWork.class);
        UserVolunteerWork mockUserVolunteerWork = mock(UserVolunteerWork.class);
        User mockUser = mock(User.class);

        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(volunteerWorkId))
                .thenReturn(Optional.of(List.of(mockUserVolunteerWork)));
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
    @DisplayName("봉사 활동 삭제 실패: 존재하지 않는 봉사 활동 ID")
    void delete_Fail_NonExistentVolunteerWork() {
        // Given
        Long nonExistentVolunteerWorkId = 999L;

        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(nonExistentVolunteerWorkId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            volunteerService.delete(nonExistentVolunteerWorkId);
        });

        verify(userVolunteerRepository).findAllByVolunteerWorkIdWithFetch(nonExistentVolunteerWorkId);
    }

    @Test
    @DisplayName("봉사 활동 삭제 실패: 삭제할 UserVolunteerWork 없음")
    void delete_Fail_NoUserVolunteerWork() {
        // Given
        Long volunteerWorkId = 1L;

        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(volunteerWorkId))
                .thenReturn(Optional.empty());


        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            volunteerService.delete(volunteerWorkId);
        });

        verify(userVolunteerRepository).findAllByVolunteerWorkIdWithFetch(volunteerWorkId);
        verify(volunteerWorkRepository, never()).delete(any(VolunteerWork.class));
    }

    @Test
    @DisplayName("봉사 활동 삭제 실패: 삭제할 UserVolunteerWork은 있으나 VolunteerWork은 없음")
    void delete_Failure_UserVolunteerWorkExists_But_VolunteerWorkNotFound() {
        // Given: 봉사활동 ID와 관련된 UserVolunteerWork는 있지만 VolunteerWork는 없음
        Long volunteerWorkId = 1L;

        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        List<UserVolunteerWork> userVolunteerWorks = List.of(
                userVolunteerWork
        );


        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(anyLong()))
                .thenReturn(Optional.of(userVolunteerWorks));

        when(volunteerWorkRepository.findById(volunteerWorkId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            volunteerService.delete(volunteerWorkId);
        });

        verify(userVolunteerRepository, times(1)).findAllByVolunteerWorkIdWithFetch(volunteerWorkId);
        verify(volunteerWorkRepository, times(1)).findById(volunteerWorkId);
        verify(volunteerWorkRepository, never()).delete(any(VolunteerWork.class));  // 삭제 시도하지 않았는지 확인
    }

}
