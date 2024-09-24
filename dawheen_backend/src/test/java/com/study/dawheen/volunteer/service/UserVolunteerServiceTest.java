package com.study.dawheen.volunteer.service;

import com.study.dawheen.config.TestSecurityConfig;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class UserVolunteerServiceTest {


    @Mock
    private UserVolunteerRepository userVolunteerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserVolunteerService userVolunteerService;

    private List<UserVolunteerWork> userVolunteerWorks;
    private VolunteerWork volunteerWork;

    @BeforeEach
    void setUp() {
        // Mock 데이터를 설정
        User user1 = mock(User.class);
        UserVolunteerWork userVolunteerWork1 = mock(UserVolunteerWork.class);
        lenient().when(userVolunteerWork1.getUser()).thenReturn(user1);

        User user2 = mock(User.class);
        UserVolunteerWork userVolunteerWork2 = mock(UserVolunteerWork.class);
        lenient().when(userVolunteerWork2.getUser()).thenReturn(user2);

        userVolunteerWorks = List.of(userVolunteerWork1, userVolunteerWork2);
        volunteerWork = mock(VolunteerWork.class);
    }

    @Test
    @DisplayName("Delete 성공")
    void deleteSuccess(){

    }

    @Test
    @DisplayName("Delete 실패 : EntityNotFound")
    void deleteFailedByEntityNotFound(){

    }



    @Test
    @DisplayName("유저_봉사활동 분리 성공")
    void unregisterUsersFromVolunteerWork_Success() {
        // given

        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(anyLong())).thenReturn(Optional.of(userVolunteerWorks));

        // when
        userVolunteerService.unregisterUsersFromVolunteerWork(1L);

        // then
        // 각 사용자가 봉사활동에서 분리되었는지 확인
        for (UserVolunteerWork userVolunteerWork : userVolunteerWorks) {
            verify(userVolunteerWork.getUser(), times(1)).leaveVolunteerWork(userVolunteerWork);
        }

        // 사용자 저장 확인
        verify(userRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("유저_봉사활동 분리 실패: userVolunteer 테이블 미존재")
    void unregisterUsersFromVolunteerWork_EntityNotFoundException() {
        // given
        when(userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            userVolunteerService.unregisterUsersFromVolunteerWork(1L);
        });

        // 사용자 저장이 호출되지 않았는지 확인
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("승인 유저_봉사활동 분리 성공")
    void deleteApprovedUserVolunteerWorkSuccess_Approved() {
        // given
        Long userVolunteerWorkId = 1L;
        User mockUser = mock(User.class);
        VolunteerWork mockVolunteerWork = mock(VolunteerWork.class);
        UserVolunteerWork mockUserVolunteerWork = mock(UserVolunteerWork.class);

        when(mockUserVolunteerWork.getStatus()).thenReturn(ApplyStatus.APPROVED);
        when(mockUserVolunteerWork.getUser()).thenReturn(mockUser);
        when(mockUserVolunteerWork.getVolunteerWork()).thenReturn(mockVolunteerWork);
        when(userVolunteerRepository.findById(userVolunteerWorkId)).thenReturn(Optional.of(mockUserVolunteerWork));

        // when
        userVolunteerService.deleteApprovedUserVolunteerWork(userVolunteerWorkId);

        // then
        verify(mockUser).leaveVolunteerWork(mockUserVolunteerWork);
        verify(mockVolunteerWork).leaveUser(mockUserVolunteerWork);
        verify(userVolunteerRepository).delete(mockUserVolunteerWork);
    }
}
