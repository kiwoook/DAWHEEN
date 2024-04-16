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
        return volunteerWorkRepository.getByRadiusAndBeforeEndDate(latitude, longitude, radius).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<VolunteerInfoResponseDto> getAllVolunteersByOrganization(Long organizationId) {
        Organization organization = organRepository.findById(organizationId).orElseThrow(EntityNotFoundException::new);

        List<VolunteerWork> volunteerWorks = volunteerWorkRepository.getAllByOrganization(organization).orElseThrow(EntityNotFoundException::new);

        return volunteerWorks.stream().map(VolunteerInfoResponseDto::new).toList();

    }

}
