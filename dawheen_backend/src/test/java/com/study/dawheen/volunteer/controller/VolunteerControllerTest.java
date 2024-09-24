package com.study.dawheen.volunteer.controller;

import com.study.dawheen.chat.repository.ChatMessageRepository;
import com.study.dawheen.common.dto.CoordinateDto;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import com.study.dawheen.volunteer.service.VolunteerQueryService;
import com.study.dawheen.volunteer.service.VolunteerService;
import com.study.dawheen.volunteer.service.VolunteerUserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(VolunteerController.class)
@ExtendWith(SpringExtension.class)
class VolunteerControllerTest {


    private static final String API_URL = "/api/v1/volunteer";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerService volunteerService;

    @MockBean
    private VolunteerQueryService volunteerQueryService;

    @MockBean
    private VolunteerUserQueryService volunteerUserQueryService;

    @MockBean
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private MockMvcAutoConfiguration mockMvcAutoConfiguration;

    @Test
    @WithMockUser(username = "user@example.com", roles = "MEMBER")
    @DisplayName("getVolunteerListWithInRadius 성공")
    void getVolunteerListWithInRadius() throws Exception {

        // given
        double latitude = 37.5665;
        double longitude = 126.9780;
        double radius = 1;

        List<VolunteerInfoResponseDto> mockVolunteerList = List.of(
                new VolunteerInfoResponseDto(
                        10, // appliedParticipants
                        50, // maxParticipants
                        1L, // id
                        LocalDateTime.now().minusDays(5), // createdDate
                        LocalDateTime.now(), // modifiedDate
                        null, // organInfoResponseDto
                        "봉사1", // title
                        "내용1", // content
                        LocalDateTime.now().plusDays(1), // serviceStartDatetime
                        LocalDateTime.now().plusDays(2), // serviceEndDatetime
                        EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), // serviceDays
                        EnumSet.of(TargetAudience.ANIMAL), // targetAudiences
                        EnumSet.of(VolunteerType.ADULT), // volunteerTypes
                        LocalDateTime.now().minusDays(10), // recruitStartDateTime
                        LocalDateTime.now().plusDays(5), // recruitEndDateTime
                        new CoordinateDto(37.5665, 126.9780) // coordinateDto
                ),
                new VolunteerInfoResponseDto(
                        10, // appliedParticipants
                        50, // maxParticipants
                        2L, // id (수정)
                        LocalDateTime.now().minusDays(5), // createdDate
                        LocalDateTime.now(), // modifiedDate
                        null, // organInfoResponseDto
                        "봉사2", // title
                        "내용2", // content
                        LocalDateTime.now().plusDays(1), // serviceStartDatetime
                        LocalDateTime.now().plusDays(2), // serviceEndDatetime
                        EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), // serviceDays
                        EnumSet.of(TargetAudience.ANIMAL), // targetAudiences
                        EnumSet.of(VolunteerType.ADULT), // volunteerTypes
                        LocalDateTime.now().minusDays(10), // recruitStartDateTime
                        LocalDateTime.now().plusDays(5), // recruitEndDateTime
                        new CoordinateDto(37.5665, 126.9780) // coordinateDto
                ));

        when(volunteerQueryService.getVolunteersWithinRadius(latitude, longitude, radius)).thenReturn(mockVolunteerList);

        // when and then
        mockMvc.perform(get(API_URL)
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude))
                        .param("radius", String.valueOf(radius)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("봉사1"))
                .andExpect(jsonPath("$[1].id").value(2L)) // id 수정
                .andExpect(jsonPath("$[1].title").value("봉사2"));

        verify(volunteerQueryService).getVolunteersWithinRadius(latitude, longitude, radius);
    }


    @Test
    void createVolunteer() {
    }

    @Test
    void getVolunteerWorkInfo() {
    }

    @Test
    void getVolunteerListByOrganization() {
    }

    @Test
    void deleteVolunteerWork() {
    }

    @Test
    void updateVolunteerWork() {
    }
}