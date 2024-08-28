package com.study.dawheen.volunteer.repository;

import com.study.dawheen.chat.repository.ChatMessageRepository;
import com.study.dawheen.config.TestSecurityConfig;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.repository.OrganRepository;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
class UserVolunteerRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(UserVolunteerRepository.class);


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

    @BeforeEach
    void setup() {

        when(passwordEncoder.encode(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String rawPassword = invocation.getArgument(0);
                    String encodedPassword = "encoded_" + rawPassword;
                    return String.format("%1$-" + 60 + "s", encodedPassword).replace(' ', 'x');
                });

    }

    @Test
    @DisplayName("봉사 ID && UserID 로 엔티티 찾기")
    void findByVolunteerWorkIdAndUserIdTest() throws InterruptedException {
        // given

        User user1 = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .roleType(RoleType.MEMBER)
                .build();

        Long userId = userRepository.save(user1).getId();

        Organization organization = Organization.builder()
                .name("Test Organization")
                .facilityPhone("02-1234-5678")
                .email("test@organization.com")
                .facilityType("Hospital")
                .representName("John Doe")
                .build();

        organRepository.save(organization);

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
                .maxParticipants(1)
                .build();

        Long volunteerWorkId = volunteerWorkRepository.save(volunteerWork).getId();

        UserVolunteerWork userVolunteerWork = new UserVolunteerWork(user1, volunteerWork);

        Long userVolunteerWorkId = userVolunteerRepository.save(userVolunteerWork).getId();

        log.info("userId = {}, userVolunteerWorkId ={}, volunteerWorkId = {}", userId, userVolunteerWorkId, volunteerWorkId);

        // when

        UserVolunteerWork userVolunteerWork1 = userVolunteerRepository.findByVolunteerWorkIdAndUserId(volunteerWorkId, userId).orElseThrow(EntityExistsException::new);

        // then
        assertThat(userVolunteerWork1.getId()).isEqualTo(userVolunteerWorkId);
        assertThat(userVolunteerWork1).isEqualTo(userVolunteerWork);

    }
}
