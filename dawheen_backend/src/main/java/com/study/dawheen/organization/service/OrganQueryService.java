package com.study.dawheen.organization.service;

import com.study.dawheen.organization.dto.OrganInfoResponseDto;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.repository.OrganRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class OrganQueryService {

    private final OrganRepository organRepository;

    public OrganInfoResponseDto getOrgan(Long id) throws EntityNotFoundException {
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new OrganInfoResponseDto(organization);
    }

    public List<OrganInfoResponseDto> getPendingOrganList() throws EntityNotFoundException {
        return organRepository.getAllByApproved(false).orElseThrow(EntityNotFoundException::new).stream().map(OrganInfoResponseDto::new).toList();
    }

}
