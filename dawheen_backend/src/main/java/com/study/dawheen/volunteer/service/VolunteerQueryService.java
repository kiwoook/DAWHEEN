package com.study.dawheen.volunteer.service;

import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.repository.OrganRepository;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolunteerQueryService {

    private final VolunteerWorkRepository volunteerWorkRepository;
    private final OrganRepository organRepository;

    @Transactional(readOnly = true)
    public VolunteerInfoResponseDto getVolunteer(Long id) {
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new VolunteerInfoResponseDto(volunteerWork);
    }

    @Transactional(readOnly = true)
    public List<VolunteerInfoResponseDto> getVolunteersWithinRadius(double latitude, double longitude, int radius) {

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도 범위 초과");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도 범위 초과");
        }

        if (radius <= 0) {
            throw new IllegalArgumentException("지름 범위 초과");
        }

        return volunteerWorkRepository.getByRadiusAndBeforeEndDate(latitude, longitude, radius);
    }

    @Transactional(readOnly = true)
    public List<VolunteerInfoResponseDto> getAllVolunteersByOrganization(Long organizationId) {
        Organization organization = organRepository.findById(organizationId).orElseThrow(EntityNotFoundException::new);

        List<VolunteerWork> volunteerWorks = volunteerWorkRepository.getAllByOrganization(organization);

        return volunteerWorks.stream().map(VolunteerInfoResponseDto::new).toList();

    }

}
