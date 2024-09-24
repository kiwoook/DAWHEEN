package com.study.dawheen.volunteer.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dawheen.chat.repository.ChatMessageRepository;
import com.study.dawheen.common.dto.CoordinateDto;
import com.study.dawheen.custom.JpaRepositoryTest;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(OutputCaptureExtension.class)
@JpaRepositoryTest
class VolunteerWorkJpaRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(VolunteerWorkJpaRepositoryTest.class);

    @Mock
    private JPAQueryFactory queryFactory;

    @Autowired
    private VolunteerWorkRepository volunteerWorkRepository;

    @MockBean
    private ChatMessageRepository chatMessageRepository;



    private Organization organization;

    @BeforeEach
    void setUp() {
        organization = Organization.builder().name("Test Organization").facilityPhone("02-1234-5678").email("test@organization.com").facilityType("Hospital").representName("John Doe").build();

    }

    @Test
    @DisplayName("getByRadiusAndBeforeEndDate 성공 테스트")
    void testGetByRadiusAndBeforeEndDate_Success() {
        // given
        double latitude = 0;  // 테스트용 위도
        double longitude = 0;  // 테스트용 경도
        double radius = 5;  // 반경 5km
        LocalDateTime now = LocalDateTime.now();  // 현재 시간
        int successCase = 10;
        int failedCase = 20;

        List<VolunteerWork> volunteerWorkList = new ArrayList<>();
        // 5km 범위에 있는 성공 케이스
        for (int i = 1; i < successCase + 1; i++) {
            VolunteerWork volunteerWork = VolunteerWork.builder().organization(organization).title("success_volunteerWork" + i).content("Assist with organizing community events and providing support to participants.").serviceStartDatetime(LocalDateTime.of(2024, 9, 21, 9, 0)).serviceEndDatetime(LocalDateTime.of(2024, 9, 21, 17, 0)).recruitStartDateTime(LocalDateTime.of(2024, 9, 1, 0, 0)).recruitEndDateTime(now.plusDays(1)).maxParticipants(100).build();

            double vLatitude = 0.0449 - (0.0001 * i);

            CoordinateDto coordinateDto = new CoordinateDto(vLatitude, longitude);
            volunteerWork.updateCoordinate(coordinateDto);
            volunteerWorkList.add(volunteerWork);
        }

        // 5KM 밖에 있는 실패 케이스
        for (int i = 1; i < failedCase + 1; i++) {
            VolunteerWork volunteerWork = VolunteerWork.builder().organization(organization).title("failed_volunteerWork" + i).content("Assist with organizing community events and providing support to participants.").serviceStartDatetime(LocalDateTime.of(2024, 9, 21, 9, 0)).serviceEndDatetime(LocalDateTime.of(2024, 9, 21, 17, 0)).recruitStartDateTime(LocalDateTime.of(2024, 9, 1, 0, 0)).recruitEndDateTime(now.plusDays(1)).maxParticipants(100).build();

            double vLatitude = 0.0449 + (0.0001 * i);

            CoordinateDto coordinateDto = new CoordinateDto(vLatitude, longitude);
            volunteerWork.updateCoordinate(coordinateDto);
            volunteerWorkList.add(volunteerWork);
        }

        volunteerWorkRepository.saveAll(volunteerWorkList);

        // when
        List<VolunteerInfoResponseDto> result = volunteerWorkRepository.getByRadiusAndBeforeEndDate(latitude, longitude, radius);

        // then
        assertThat(result).hasSize(successCase);
    }

    @Test
    @DisplayName("getByFiltersAndDataRangeWithinRadius 성공1")
    void testGetByFiltersAndDataRangeWithinRadius1() {

        // given
        List<VolunteerWork> volunteerWorkList = new ArrayList<>();
        double latitude = 0;  // 테스트용 위도
        double longitude = 0;  // 테스트용 경도
        int successCase = 10;

        LocalDateTime now = LocalDateTime.now();  // 현재 시간
        LocalDateTime startDateTime = LocalDateTime.of(2024, 9, 21, 9, 0);
        LocalDateTime endDateTime = startDateTime.plusDays(1);

        for (int i = 1; i < successCase + 1; i++) {
            VolunteerWork volunteerWork = VolunteerWork.builder()
                    .organization(organization).title("success_volunteerWork" + i)
                    .content("content")
                    .serviceStartDatetime(startDateTime)
                    .serviceEndDatetime(endDateTime)
                    .recruitStartDateTime(LocalDateTime.of(2024, 9, 1, 0, 0))
                    .recruitEndDateTime(now.plusDays(1))
                    .maxParticipants(100)
                    .volunteerTypes(Set.of(VolunteerType.ADULT))
                    .targetAudiences(Set.of(TargetAudience.ANIMAL))
                    .build();

            CoordinateDto coordinateDto = new CoordinateDto(latitude, longitude);
            volunteerWork.updateCoordinate(coordinateDto);
            volunteerWorkList.add(volunteerWork);
        }

        volunteerWorkRepository.saveAll(volunteerWorkList);

        // when

        List<VolunteerInfoResponseDto> result = volunteerWorkRepository.getByFiltersAndDataRangeWithinRadius(latitude, longitude, 1,
                Set.of(VolunteerType.ADULT, VolunteerType.YOUTH), Set.of(TargetAudience.ANIMAL),
                startDateTime.minusDays(1),
                endDateTime.plusDays(1)
        );

        // then
        assertThat(result).hasSize(successCase);
    }

    @Test
    @DisplayName("getByFiltersAndDataRangeWithinRadius 성공1 : 전체 탐색 필터링 값 미존재")
    void testGetByFiltersAndDataRangeWithinRadius2() {

        // given
        List<VolunteerWork> volunteerWorkList = new ArrayList<>();
        double latitude = 0;  // 테스트용 위도
        double longitude = 0;  // 테스트용 경도
        int successCase = 10;

        LocalDateTime now = LocalDateTime.now();  // 현재 시간
        LocalDateTime startDateTime = LocalDateTime.of(2024, 9, 21, 9, 0);
        LocalDateTime endDateTime = startDateTime.plusDays(1);

        for (int i = 1; i < successCase + 1; i++) {
            VolunteerWork volunteerWork = VolunteerWork.builder()
                    .organization(organization).title("success_volunteerWork" + i)
                    .content("content")
                    .serviceStartDatetime(startDateTime)
                    .serviceEndDatetime(endDateTime)
                    .recruitStartDateTime(LocalDateTime.of(2024, 9, 1, 0, 0))
                    .recruitEndDateTime(now.plusDays(1))
                    .maxParticipants(100)
                    .volunteerTypes(Set.of(VolunteerType.ADULT))
                    .targetAudiences(Set.of(TargetAudience.ANIMAL))
                    .build();

            CoordinateDto coordinateDto = new CoordinateDto(latitude, longitude);
            volunteerWork.updateCoordinate(coordinateDto);
            volunteerWorkList.add(volunteerWork);
        }

        volunteerWorkRepository.saveAll(volunteerWorkList);

        // when

        List<VolunteerInfoResponseDto> result = volunteerWorkRepository.getByFiltersAndDataRangeWithinRadius(latitude, longitude, 1,
                Set.of(), Set.of(),
                startDateTime.minusDays(1),
                endDateTime.plusDays(1)
        );

        // then
        assertThat(result).hasSize(successCase);

    }

    @Test
    @DisplayName("getByFiltersAndDataRangeWithinRadius 실패 : ")
    void failedTestGetByFiltersAndDataRangeWithinRadius() {
        // given
        List<VolunteerWork> volunteerWorkList = new ArrayList<>();
        double latitude = 0;  // 테스트용 위도
        double longitude = 0;  // 테스트용 경도
        int failedCase = 10;

        LocalDateTime now = LocalDateTime.now();  // 현재 시간
        LocalDateTime startDateTime = LocalDateTime.of(2024, 9, 21, 9, 0);
        LocalDateTime endDateTime = startDateTime.plusDays(1);

        for (int i = 1; i < failedCase + 1; i++) {
            VolunteerWork volunteerWork = VolunteerWork.builder()
                    .organization(organization).title("success_volunteerWork" + i)
                    .content("content")
                    .serviceStartDatetime(startDateTime)
                    .serviceEndDatetime(endDateTime)
                    .recruitStartDateTime(LocalDateTime.of(2024, 9, 1, 0, 0))
                    .recruitEndDateTime(now.plusDays(1))
                    .maxParticipants(100)
                    .volunteerTypes(Set.of(VolunteerType.ADULT))
                    .targetAudiences(Set.of(TargetAudience.ANIMAL))
                    .build();

            CoordinateDto coordinateDto = new CoordinateDto(latitude, longitude);
            volunteerWork.updateCoordinate(coordinateDto);
            volunteerWorkList.add(volunteerWork);
        }

        volunteerWorkRepository.saveAll(volunteerWorkList);

        // when

        List<VolunteerInfoResponseDto> result = volunteerWorkRepository.getByFiltersAndDataRangeWithinRadius(latitude, longitude, 1,
                Set.of(VolunteerType.YOUTH), Set.of(TargetAudience.ANIMAL),
                startDateTime.minusDays(1),
                endDateTime.plusDays(1)
        );

        // then
        assertThat(result).isEmpty();
    }





}
