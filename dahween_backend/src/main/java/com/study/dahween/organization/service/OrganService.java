package com.study.dahween.organization.service;

import com.study.dahween.organization.dto.OrganCreateRequestDto;
import com.study.dahween.organization.dto.OrganInfoResponseDto;
import com.study.dahween.organization.entity.Organization;
import com.study.dahween.organization.repository.OrganRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganService {
    private final OrganRepository organRepository;

    public OrganInfoResponseDto getOrgan(Long id) {
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return new OrganInfoResponseDto(organization);
    }

    @Transactional
    public void create(OrganCreateRequestDto requestDto){
        Organization organization = Organization.toEntity(requestDto);

        organRepository.save(organization);
    }

    @Transactional
    public void delete(Long id) throws EmptyResultDataAccessException {
        organRepository.deleteById(id);
    }
}
