package com.study.dawheen.volunteer.repository;

import com.study.dawheen.chat.repository.ChatMessageRepository;
import com.study.dawheen.custom.JpaRepositoryTest;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.repository.OrganRepository;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
@JpaRepositoryTest
class UserVolunteerJpaRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(UserVolunteerJpaRepositoryTest.class);

    @Autowired
    UserVolunteerRepository userVolunteerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VolunteerWorkRepository volunteerWorkRepository;

    @Autowired
    OrganRepository organRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @MockBean
    private ChatMessageRepository chatMessageRepository;

    private User user;
    private String email;
    private Long userId;
    private Organization organization;
    private VolunteerWork volunteerWork;
    private Long volunteerWorkId;
    private UserVolunteerWork userVolunteerWork;

    @BeforeEach
    void setup() {
        when(passwordEncoder.encode(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String rawPassword = invocation.getArgument(0);
                    String encodedPassword = "encoded_" + rawPassword;
                    return String.format("%1$-" + 60 + "s", encodedPassword).replace(' ', 'x');
                });

        user = User.builder()
                .name("user0")
                .email("user0@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .roleType(RoleType.MEMBER)
                .build();

        email = user.getEmail();
        userId = userRepository.save(user).getId();

        organization = Organization.builder()
                .name("Test Organization")
                .facilityPhone("02-1234-5678")
                .email("test@organization.com")
                .facilityType("Hospital")
                .representName("John Doe")
                .build();

        organRepository.save(organization);

        volunteerWork = VolunteerWork.builder()
                .organization(organization)
                .title("Sample Volunteer Work")
                .content("This is a sample content.")
                .serviceStartDatetime(LocalDateTime.of(2024, 1, 1, 9, 0))
                .serviceEndDatetime(LocalDateTime.of(2024, 12, 31, 17, 0))
                .serviceDays(Set.of(LocalDate.now().getDayOfWeek()))
                .targetAudiences(Set.of(TargetAudience.ANIMAL))
                .volunteerTypes(Set.of(VolunteerType.ADULT))
                .recruitStartDateTime(LocalDateTime.now())
                .recruitEndDateTime(LocalDateTime.now().plusMonths(1))
                .maxParticipants(1)
                .build();

        volunteerWorkId = volunteerWorkRepository.save(volunteerWork).getId();

        userVolunteerWork = new UserVolunteerWork(user, volunteerWork);

        userVolunteerRepository.save(userVolunteerWork);
    }

    @Test
    @DisplayName("봉사 ID && UserID 로 엔티티 찾기")
    void findByVolunteerWorkIdAndUserIdTest() {
        // given

        Long userVolunteerWorkId = userVolunteerRepository.save(userVolunteerWork).getId();

        log.info("userId = {}, userVolunteerWorkId ={}, volunteerWorkId = {}", userId, userVolunteerWorkId, volunteerWorkId);

        // when
        UserVolunteerWork userVolunteerWork1 = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityExistsException::new);

        // then
        assertThat(userVolunteerWork1.getId()).isEqualTo(userVolunteerWorkId);
        assertThat(userVolunteerWork1).isEqualTo(userVolunteerWork);

    }

    @Test
    @DisplayName("existsByVolunteerWorkAndUserIdAndStatus 성공 테스트")
    void testExistsByVolunteerWorkAndUserIdAndStatus() {
        // given

        userVolunteerWork.updateStatus(ApplyStatus.APPROVED);
        userVolunteerRepository.save(userVolunteerWork);

        List<ApplyStatus> statuses = List.of(ApplyStatus.APPROVED);

        // when
        boolean result = userVolunteerRepository.existsByVolunteerWorkAndUserIdAndStatus(volunteerWorkId, userId, statuses);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("existsByVolunteerWorkAndUserIdAndStatus 실패 테스트")
    void failedTestExistsByVolunteerWorkAndUserIdAndStatus() {
        // given
        userVolunteerWork.updateStatus(ApplyStatus.APPROVED);

        userVolunteerRepository.save(userVolunteerWork);

        List<ApplyStatus> statuses = List.of(ApplyStatus.PENDING, ApplyStatus.COMPLETED, ApplyStatus.REJECTED);

        // when
        boolean result = userVolunteerRepository.existsByVolunteerWorkAndUserIdAndStatus(volunteerWorkId, userId, statuses);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("existsByVolunteerWorkAndEmailAndStatus 실패 테스트")
    void existsByVolunteerWorkAndEmailAndStatus_shouldReturnFalse_whenNoRecordExists() {
        // Given

        userVolunteerWork.updateStatus(ApplyStatus.APPROVED);

        userVolunteerRepository.save(userVolunteerWork);

        List<ApplyStatus> statuses = List.of(ApplyStatus.REJECTED, ApplyStatus.PENDING, ApplyStatus.COMPLETED);

        // When
        boolean exists = userVolunteerRepository.existsByVolunteerWorkAndEmailAndStatus(volunteerWorkId, email, statuses);

        // Then
        assertFalse(exists);
    }

    @Test
    @DisplayName("findAllByVolunteerWorkIdWithFetch 성공 테스트")
    void findAllByVolunteerWorkIdWithFetch_shouldReturnUserVolunteerWorkList() {
        // Given
        final int userSize = 100;
        List<User> userList = new ArrayList<>();

        for (int i = 1; i < userSize + 1; i++) {
            userList.add(
                    User.builder()
                            .name("user" + i)
                            .email("user" + i + "@gmail.com")
                            .password(passwordEncoder.encode("1234"))
                            .roleType(RoleType.MEMBER)
                            .build()
            );
        }

       userRepository.saveAll(userList);

        List<UserVolunteerWork> userVolunteerWorkList = new ArrayList<>();
        for (User eachUser : userList) {
            userVolunteerWorkList.add(new UserVolunteerWork(eachUser, volunteerWork));
        }

        List<UserVolunteerWork> expectList = userVolunteerRepository.saveAll(userVolunteerWorkList);

        // When
        Optional<List<UserVolunteerWork>> result = userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(volunteerWorkId);

        // Fetch 가 정상적으로 돌아가는 지 확인해보는 쿼리 확인
        assertThat(result).isPresent();

        log.info("Fetch 동작 확인");

        List<User> resultUserList = new ArrayList<>();
        for (UserVolunteerWork eachUserVolunteerWork : result.get()) {
            resultUserList.add(eachUserVolunteerWork.getUser());
        }

        // Then
        assertThat(result.get()).hasSize(userSize + 1);

    }

    @Test
    @DisplayName("findUsersByVolunteerWorkIdAndStatus 성공 테스트")
    void findUsersByVolunteerWorkIdAndStatus_shouldReturnUserList() {
        // Given
        final int userSize = 200;
        List<User> userList = new ArrayList<>();

        for (int i = 1; i < userSize + 1; i++) {
            userList.add(
                    User.builder()
                            .name("user" + i)
                            .email("user" + i + "@gmail.com")
                            .password(passwordEncoder.encode("1234"))
                            .roleType(RoleType.MEMBER)
                            .build()
            );
        }


        userList = userRepository.saveAll(userList);

        List<User> approvedUser = userList.subList(0, userSize / 2);
        List<User> pendingUser = userList.subList(userSize / 2, userList.size());


        List<UserVolunteerWork> userVolunteerWorkList = new ArrayList<>();
        for (User eachUser : approvedUser) {
            UserVolunteerWork eachUserVolunteerWork = new UserVolunteerWork(eachUser, volunteerWork);
            eachUserVolunteerWork.updateStatus(ApplyStatus.APPROVED);
            userVolunteerWorkList.add(eachUserVolunteerWork);
        }

        userVolunteerRepository.saveAll(userVolunteerWorkList);

        userVolunteerWorkList.clear();

        for (User eachUser : pendingUser) {
            userVolunteerWorkList.add(new UserVolunteerWork(eachUser, volunteerWork));
        }

        userVolunteerRepository.saveAll(userVolunteerWorkList);

        // When
        List<User> result = userVolunteerRepository.findUsersByVolunteerWorkIdAndStatus(volunteerWorkId, ApplyStatus.APPROVED).orElseThrow(EntityNotFoundException::new);


        // Then
        assertThat(result).hasSize(userSize / 2);
        assertThat(result).isEqualTo(approvedUser);
    }


    @Test
    void findByVolunteerWorkIdAndUserId_shouldReturnUserVolunteerWork() {
        // Given

        // When
        UserVolunteerWork result = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityNotFoundException::new);

        // N + 1 문제 확인하기
        log.info("추가 쿼리 나가는 지 확인");

        // Then
        assertThat(result).isEqualTo(userVolunteerWork);
        assertThat(userId).isEqualTo(result.getUser().getId());
        assertThat(volunteerWorkId).isEqualTo(result.getVolunteerWork().getId());
    }

    @Test
    @DisplayName("findByVolunteerWorkIdAndEmail 테스트")
    void testFindByVolunteerWorkIdAndEmail() {
        // Given

        UserVolunteerWork result = userVolunteerRepository.findByVolunteerWorkIdAndEmail(volunteerWorkId, email).orElseThrow(EntityNotFoundException::new);

        // N + 1 문제 확인하기
        log.info("추가 쿼리 나가는 지 확인");

        // Then
        assertThat(result).isEqualTo(userVolunteerWork);
        assertThat(email).isEqualTo(result.getUser().getEmail());
        assertThat(volunteerWorkId).isEqualTo(result.getVolunteerWork().getId());
    }

    @Test
    @DisplayName("findVolunteerWorkByEmailAndStatus 성공 테스트")
    void testFindVolunteerWorkByEmailAndStatus() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<VolunteerInfoResponseDto> result =
                userVolunteerRepository.findVolunteerWorkByEmailAndStatus(email, ApplyStatus.PENDING, pageable);

        assertThat(result).isNotNull(); // 결과가 null이 아닌지 확인
        assertThat(result.getTotalElements()).isEqualTo(1); // 총 1개의 봉사활동이 반환되어야 함
        assertThat(result.getContent()).hasSize(1); // 페이지 당 1개의 항목이 있어야 함
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(volunteerWork.getTitle()); // 봉사활동 제목 검증
    }

}
