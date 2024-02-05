package com.study.dahween.organization.controller;

import com.study.dahween.organization.dto.OrganInfoResponseDto;
import com.study.dahween.organization.dto.OrganRequestDto;
import com.study.dahween.organization.service.OrganService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganController {
    private final OrganService organService;

    // 기관 정보 확인
    @GetMapping("/{id}")
    public ResponseEntity<OrganInfoResponseDto> getOrganization(@PathVariable("id") Long id) {
        try {
            OrganInfoResponseDto organ = organService.getOrgan(id);
            return ResponseEntity.ok(organ);
        } catch (EntityNotFoundException e) {
            log.info("해당 ID가 존재하지 않습니다. id = {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // 기관 개설
    @PostMapping
    public ResponseEntity<OrganInfoResponseDto> createOrganization(@RequestBody OrganRequestDto requestDto) {
        try {
            OrganInfoResponseDto responseDto = organService.create(requestDto);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            // TODO 적절한 예외처리를 만들 필요가 있음.
            return ResponseEntity.badRequest().build();
        }
    }


    @PreAuthorize("hasRole('ROLE_ORGANIZATION') or hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OrganInfoResponseDto> updateOrganization(@PathVariable("id") Long id, @RequestBody OrganRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        log.info("유저 아이디 : {}", userId);

        try {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                    organService.verifyAffiliation(id, userId)) {
                organService.update(id, requestDto);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PreAuthorize("hasRole('ROLE_ORGANIZATION') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<OrganInfoResponseDto> deleteOrganization(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        log.info("유저 아이디 : {}", userId);

        try {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                    organService.verifyAffiliation(id, userId)) {
                organService.delete(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    // TODO 해당 기관의 후기 모음

    // TODO 특정 유저에게 해당 기관 권한 부여

    // TODO 기관 평점

}
