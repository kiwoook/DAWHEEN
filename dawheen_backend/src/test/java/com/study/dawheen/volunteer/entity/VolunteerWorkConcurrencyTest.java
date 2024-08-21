package com.study.dawheen.volunteer.entity;

import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class VolunteerWorkConcurrencyTest {

    private VolunteerWork volunteerWork;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void setup() {

        Organization mockOrganization = Mockito.mock(Organization.class);
        Mockito.when(mockOrganization.getId()).thenReturn(1L);

        volunteerWork = VolunteerWork.builder()
                .organization(mockOrganization)
                .title("Sample Volunteer Work")
                .content("This is a sample content.")
                .serviceStartDate(LocalDate.of(2024, 1, 1))
                .serviceEndDate(LocalDate.of(2024, 12, 31))
                .serviceStartTime(LocalTime.of(9, 0))
                .serviceEndTime(LocalTime.of(17, 0))
                .serviceDays(Set.of(LocalDate.now().getDayOfWeek())) // 예시로 현재 요일 사용
                .targetAudiences(Set.of(TargetAudience.ANIMAL)) // 예시로 TargetAudience.ALL 사용
                .volunteerTypes(Set.of(VolunteerType.ADULT)) // 예시로 VolunteerType.GENERAL 사용
                .recruitStartDateTime(LocalDateTime.now())
                .recruitEndDateTime(LocalDateTime.now().plusMonths(1))
                .maxParticipants(100) // maxParticipants 설정
                .build();
    }

    @Test
    void testIncreaseParticipantsConcurrency() throws InterruptedException {
        // Given

        int maxParticipants = 100;
        AtomicInteger exceedCnt = new AtomicInteger();
        int threadCount = 150;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    volunteerWork.increaseParticipants();
                } catch (IllegalStateException e) {
                    // Expected exception when maxParticipants is reached
                    exceedCnt.addAndGet(1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        assertThat(volunteerWork.getAppliedParticipants().get()).isEqualTo(maxParticipants);
        assertThat(exceedCnt.get()).isEqualTo(threadCount - maxParticipants);
    }

    @Test
    void testDecreaseParticipantsConcurrency() throws InterruptedException {
        // Given

        int threadCount = 150;
        AtomicInteger exceedCnt = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    volunteerWork.decreaseParticipants();
                } catch (IllegalStateException e) {
                    exceedCnt.addAndGet(1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        assertThat(volunteerWork.getAppliedParticipants().get()).isZero();
        assertThat(exceedCnt.get()).isEqualTo(threadCount);
    }
}