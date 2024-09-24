package com.study.dawheen.volunteer.service;

import com.study.dawheen.config.TestSecurityConfig;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.repository.OrganRepository;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class VolunteerQueryServiceTest {

    @Mock
    private VolunteerWorkRepository volunteerWorkRepository;

    @InjectMocks
    private VolunteerQueryService volunteerQueryService;

    @Mock
    private OrganRepository organRepository;

    @Mock
    private Organization organization;

    @Mock
    private PasswordEncoder passwordEncoder;

    private VolunteerWork volunteerWork;
    private User user;
    private String userEmail;

    @BeforeEach
    void setup() {
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
    void testGetVolunteer_Success() {
        Long validId = 1L;
        when(volunteerWorkRepository.findById(validId)).thenReturn(Optional.of(volunteerWork));

        VolunteerInfoResponseDto result = volunteerQueryService.getVolunteer(validId);

        assertNotNull(result);
        assertEquals(volunteerWork.getId(), result.getId()); // Adjust based on actual properties
    }

    @Test
    void testGetVolunteer_NotFound() {
        Long invalidId = 999L;
        when(volunteerWorkRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> volunteerQueryService.getVolunteer(invalidId));
    }

    @Test
    void testGetVolunteer_NullOrInvalidId() {
        assertThrows(EntityNotFoundException.class, () -> volunteerQueryService.getVolunteer(null));

        assertThrows(EntityNotFoundException.class, () -> volunteerQueryService.getVolunteer(0L));

        assertThrows(EntityNotFoundException.class, () -> volunteerQueryService.getVolunteer(-1L));
    }

    @Test
    void testGetVolunteersWithinRadius_Success() {
        double latitude = 37.7749;
        double longitude = -122.4194;
        double radius = 10;
        List<VolunteerInfoResponseDto> volunteerWorks = new ArrayList<>();

        when(volunteerWorkRepository.getByRadiusAndBeforeEndDate(latitude, longitude, radius))
                .thenReturn(volunteerWorks);

        List<VolunteerInfoResponseDto> result = volunteerQueryService.getVolunteersWithinRadius(latitude, longitude, radius);

        assertNotNull(result);
        assertEquals(volunteerWorks.size(), result.size()); // Adjust based on actual properties
    }

    @Test
    void testGetVolunteersWithinRadius_NoResults() {
        double latitude = 37.7749;
        double longitude = -122.4194;
        double radius = 10;
        when(volunteerWorkRepository.getByRadiusAndBeforeEndDate(latitude, longitude, radius))
                .thenReturn(Collections.emptyList());

        List<VolunteerInfoResponseDto> result = volunteerQueryService.getVolunteersWithinRadius(latitude, longitude, radius);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetVolunteersWithinRadius_InvalidCoordinates() {
        double invalidLatitude = 100.0; // Invalid latitude
        double invalidLongitude = -200.0; // Invalid longitude
        double radius = 10;

        assertThrows(IllegalArgumentException.class, () -> volunteerQueryService.getVolunteersWithinRadius(invalidLatitude, invalidLongitude, radius));
    }

    @Test
    void testGetVolunteersWithinRadius_InvalidRadius() {
        double latitude = 37.7749;
        double longitude = -122.4194;

        assertThrows(IllegalArgumentException.class, () -> volunteerQueryService.getVolunteersWithinRadius(latitude, longitude, 0));

        assertThrows(IllegalArgumentException.class, () -> volunteerQueryService.getVolunteersWithinRadius(latitude, longitude, -10));
    }

    @Test
    void testGetAllVolunteersByOrganization_Success() {
        Long validOrganizationId = 1L;
        List<VolunteerWork> volunteerWorks = new ArrayList<>(); // Set up with valid data
        when(organRepository.findById(validOrganizationId)).thenReturn(Optional.of(organization));
        when(volunteerWorkRepository.getAllByOrganization(organization)).thenReturn(volunteerWorks);

        List<VolunteerInfoResponseDto> result = volunteerQueryService.getAllVolunteersByOrganization(validOrganizationId);

        assertNotNull(result);
        assertEquals(volunteerWorks.size(), result.size()); // Adjust based on actual properties
    }

    @Test
    void testGetAllVolunteersByOrganization_NotFound() {
        Long invalidOrganizationId = 999L;
        when(organRepository.findById(invalidOrganizationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> volunteerQueryService.getAllVolunteersByOrganization(invalidOrganizationId));
    }

    @Test
    void testGetAllVolunteersByOrganization_NoVolunteers() {
        Long validOrganizationId = 1L;
        when(organRepository.findById(validOrganizationId)).thenReturn(Optional.of(organization));
        when(volunteerWorkRepository.getAllByOrganization(organization)).thenReturn(Collections.emptyList());

        List<VolunteerInfoResponseDto> result = volunteerQueryService.getAllVolunteersByOrganization(validOrganizationId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllVolunteersByOrganization_InvalidId() {
        assertThrows(EntityNotFoundException.class, () -> volunteerQueryService.getAllVolunteersByOrganization(null));
        assertThrows(EntityNotFoundException.class, () -> volunteerQueryService.getAllVolunteersByOrganization(0L));
        assertThrows(EntityNotFoundException.class, () -> volunteerQueryService.getAllVolunteersByOrganization(-1L));
    }
}
