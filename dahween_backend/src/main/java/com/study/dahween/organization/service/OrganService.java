package com.study.dahween.organization.service;

import com.study.dahween.organization.dto.OrganInfoResponseDto;
import com.study.dahween.organization.dto.OrganRequestDto;
import com.study.dahween.organization.entity.Organization;
import com.study.dahween.organization.repository.OrganRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganService {
    private final OrganRepository organRepository;

    public OrganInfoResponseDto getOrgan(Long id) throws EntityNotFoundException {
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new OrganInfoResponseDto(organization);
    }

    @Transactional
    public OrganInfoResponseDto create(OrganRequestDto requestDto) {
        Organization organization = Organization.toEntity(requestDto);
        Organization savedOrgan = organRepository.save(organization);

        return new OrganInfoResponseDto(savedOrgan);
    }

    @Transactional
    public void delete(Long id) throws EmptyResultDataAccessException {
        log.info("기관 ID : {}인 엔티티 삭제", id);
        organRepository.deleteById(id);
    }

    @Transactional
    public void update(Long id, OrganRequestDto requestDto) {
        Organization organization = organRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        organization.update(requestDto);
    }

}
