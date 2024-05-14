package com.study.dawheen.organization.controller;

import com.study.dawheen.organization.dto.OrganInfoResponseDto;
import com.study.dawheen.organization.dto.OrganRequestDto;
import com.study.dawheen.organization.service.OrganQueryService;
import com.study.dawheen.organization.service.OrganService;
import com.study.dawheen.organization.service.OrganSubscribeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
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

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "기관", description = "기관 CRUD 및 관련 API")
public class OrganController {

    private final OrganService organService;
    private final OrganQueryService organQueryService;
    private final OrganSubscribeService organSubscribeService;

    @Operation(summary = "기관 정보", description = "기관의 ID 값을 통해 기관 정보를 반환합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<OrganInfoResponseDto> getOrganization(@PathVariable("id") Long id) {
        OrganInfoResponseDto organ = organQueryService.getOrgan(id);
        return ResponseEntity.ok(organ);
    }

    @Operation(summary = "기관 찾기", description = "위도와 경도 그리고 범위(m)를 사용해 주변에 존재하는 기관들을 반환합니다.")
    @GetMapping("/find")
    public ResponseEntity<List<OrganInfoResponseDto>> getOrganizationInfoWithinRadius(
            @Parameter(name = "경도", required = true) @RequestParam double latitude,
            @Parameter(name = "경도", required = true) @RequestParam double longitude,
            @Parameter(name = "반경", description = "단위 : m", required = true) @RequestParam int radius) {

        List<OrganInfoResponseDto> organInfoResponseDtos = organService.findOrganizationsWithinRadius(latitude, longitude, radius);
        return ResponseEntity.ok(organInfoResponseDtos);
    }

    @Operation(summary = "기관 정보 업데이트")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION') or hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OrganInfoResponseDto> updateOrganization(@PathVariable("id") Long id, @RequestBody @Valid OrganRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                    organService.verifyAffiliation(id, email)) {
                organService.update(id, requestDto);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @Operation(summary = "기관 정보 삭제")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<OrganInfoResponseDto> deleteOrganization(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
                    organService.verifyAffiliation(id, email)) {
                organService.delete(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // TODO 해당 기관의 후기 모음

    // TODO 기관 평점

    @Operation(summary = "기관 등록 신청")
    @PostMapping("/apply")
    public ResponseEntity<OrganInfoResponseDto> applyOrganization(@RequestBody @Valid OrganRequestDto requestDto) {
        try {
            OrganInfoResponseDto responseDto = organService.create(requestDto);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "기관 신청 대기 명단", description = "신청 대기 중인 기관들을 확인합니다.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrganInfoResponseDto>> getPendingOrganizationList() {
        try {
            return ResponseEntity.ok(organQueryService.getPendingOrganList());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "대기 기관 수락", description = "대기중인 해당 기관을 수락합니다. ")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/enroll/{id}")
    public ResponseEntity<OrganInfoResponseDto> enrollOrganization(@PathVariable Long id) {
        try {
            organService.enroll(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "대기 기관 거절", description = "대기중인 해당 기관을 거절합니다. ")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/denied/{id}")
    public ResponseEntity<OrganInfoResponseDto> deniedOrganization(@PathVariable Long id) {
        try {
            organService.denied(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "사용자 기관 권한 부여")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION') or hasRole('ROLE_ADMIN')")
    @PostMapping("/grant")
    public ResponseEntity<OrganInfoResponseDto> grantRole(@RequestBody RoleRequestDto roleRequestDto) {
        try {
            organService.grantOrganizationRole(roleRequestDto.getEmail(), roleRequestDto.getOrganizationId());
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "사용자 기관 권한 삭제")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION') or hasRole('ROLE_ADMIN')")
    @PostMapping("/revoke")
    public ResponseEntity<OrganInfoResponseDto> revokeRole(@RequestBody @Valid RoleRequestDto roleRequestDto) {
        try {
            organService.revokeOrganizationRole(roleRequestDto.getEmail(), roleRequestDto.getOrganizationId());
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @Operation(summary = "사용자 기관 구독", description = "사용자가 기관을 구독하여 봉사활동 생성 시 알림을 발송합니다.")
    @PostMapping("/subscribe")
    public ResponseEntity<Object> subscribe(@RequestBody Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        organSubscribeService.subscribe(email, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 기관 구독 취소", description = "기관 구독을 취소합니다")
    @DeleteMapping("/subscribe")
    public ResponseEntity<Object> cancelSubscribe(@RequestBody Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        organSubscribeService.cancel(email, id);
        return ResponseEntity.ok().build();
    }


    @Data
    public static class RoleRequestDto {
        @NotNull
        private String email;
        @NotNull
        private Long organizationId;
    }
}
