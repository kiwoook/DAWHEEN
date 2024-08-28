package com.study.dawheen.volunteer.integration;

import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.repository.OrganRepository;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import com.study.dawheen.volunteer.service.VolunteerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(OutputCaptureExtension.class)
class UserVolunteerServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(VolunteerService.class);
    @Autowired
    OrganRepository organRepository;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    @Autowired
    private VolunteerService volunteerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VolunteerWorkRepository volunteerWorkRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserVolunteerRepository userVolunteerRepository;

    @BeforeEach
    void setUp() {

    }

    @Disabled
    @Test
    @DisplayName("동시성 테스트: 여러 사용자가 동시에 봉사 활동에 신청을 시도")
    void testConcurrencyWithPessimisticLock() throws InterruptedException {
        // Given
        String adminEmail = "admin@example.com";

        Organization organization = Organization.builder()
                .name("Test Organization")
                .facilityPhone("02-1234-5678")
                .email(adminEmail)
                .facilityType("Hospital")
                .representName("John Doe")
                .build();

        organRepository.save(organization);

        User admin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode("1234"))
                .name("admin")
                .roleType(RoleType.ORGANIZATION)
                .build();

        admin.grantOrganization(organization);
        userRepository.save(admin);

        VolunteerWork volunteerWork = VolunteerWork.builder()
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
                .maxParticipants(10)  // 최대 참가자 수를 10명으로 설정
                .build();


        volunteerWorkRepository.save(volunteerWork);
        Long volunteerWorkId = volunteerWork.getId();

        List<User> users = IntStream.range(0, 50)
                .mapToObj(i -> User.builder()
                        .email("test" + i + "@gmail.com")
                        .password(passwordEncoder.encode("1234"))
                        .name("user" + i)
                        .roleType(RoleType.MEMBER)
                        .build())
                .toList();

        userRepository.saveAll(users);

        List<UserVolunteerWork> userVolunteerWorks = users.stream()
                .map(user -> new UserVolunteerWork(user, volunteerWork))
                .toList();

        userVolunteerRepository.saveAll(userVolunteerWorks);

        int numThreads = 50; // 50명의 유저가 동시에 신청
        CountDownLatch doneSignal = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < numThreads; i++) {
            User user = users.get(i % users.size()); // 유저 목록에서 순차적으로 유저를 가져옴
            executorService.execute(() -> {
                try {
                    log.info("approved userId = {}", user.getId());
                    volunteerService.approve(adminEmail, volunteerWorkId, user.getId());
                    successCount.getAndIncrement(); // 성공한 신청 수 증가
                } catch (Exception e) {
                    e.printStackTrace();
                    failCount.getAndIncrement(); // 예외 처리
                } finally {
                    doneSignal.countDown(); // 쓰레드 작업 완료를 알림
                }
            });
        }

        // 모든 작업이 완료될 때까지 대기
        doneSignal.await();
        executorService.shutdown();
        log.info("성공 개수 : {}", successCount.get());
        // Then
        assertThat(successCount.get()).isEqualTo(volunteerWork.getMaxParticipants()); // 성공한 신청 수는 최대 인원을 초과할 수 없음
        assertThat(failCount.get()).isEqualTo(numThreads - volunteerWork.getMaxParticipants()); //
    }

    @Test
    @DisplayName("일반 테스트: 여러 사용자가 순차적으로 봉사 활동에 신청을 시도")
    void testSequentialVolunteerApply() {
        // Given
        String adminEmail = "admin@example.com";

        // 봉사 단체 생성
        Organization organization = Organization.builder()
                .name("Test Organization")
                .facilityPhone("02-1234-5678")
                .email("test@organization.com")
                .facilityType("Hospital")
                .representName("John Doe")
                .build();

        organRepository.save(organization);

        // 관리자 생성 및 단체 연결
        User admin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode("1234"))
                .name("admin")
                .roleType(RoleType.ORGANIZATION)
                .build();

        admin.grantOrganization(organization);
        userRepository.save(admin);

        // 봉사활동 생성
        VolunteerWork volunteerWork = VolunteerWork.builder()
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
                .maxParticipants(10)  // 최대 참가자 수 설정
                .build();

        volunteerWorkRepository.save(volunteerWork);
        Long volunteerWorkId = volunteerWork.getId();

        // 50명의 사용자 생성
        List<User> users = IntStream.range(0, 50)
                .mapToObj(i -> User.builder()
                        .email("test" + i + "@gmail.com")
                        .password(passwordEncoder.encode("1234"))
                        .name("user" + i)
                        .roleType(RoleType.MEMBER)
                        .build())
                .toList();

        userRepository.saveAll(users);

        // Clean UserVolunteerWork table before the test
        userVolunteerRepository.deleteAll();

        // Create UserVolunteerWork entities
        List<UserVolunteerWork> userVolunteerWorks = users.stream()
                .map(user -> new UserVolunteerWork(user, volunteerWork))
                .toList();
        userVolunteerRepository.saveAll(userVolunteerWorks);

        // 순차적으로 10명의 유저가 봉사활동 신청을 시도
        int tryCnt = 50;
        int successCount = 0;
        int failedCount = 0;
        for (int i = 0; i < tryCnt; i++) {
            User user = users.get(i);
            try {
                volunteerService.approve(adminEmail, volunteerWorkId, user.getId());
                successCount++;  // 성공한 신청 수 증가
            } catch (Exception e) {
                failedCount++;
            }
        }

        // Then
        log.info("성공 개수 : {}", successCount);
        log.info("실패 개수 : {}", failedCount);
        assertThat(successCount).isEqualTo(volunteerWork.getMaxParticipants());  // 성공한 신청 수는 최대 인원을 초과할 수 없음
        assertThat(failedCount).isEqualTo(tryCnt - volunteerWork.getMaxParticipants());
    }
}