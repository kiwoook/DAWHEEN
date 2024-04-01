package com.study.dahween.volunteer.controller;

import com.study.dahween.common.exception.AuthorizationFailedException;
import com.study.dahween.user.dto.UserInfoResponseDto;
import com.study.dahween.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dahween.volunteer.dto.VolunteerUpdateResponseDto;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import com.study.dahween.volunteer.service.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Semaphore;

@RestController
@Slf4j
@RequestMapping("/api/v1/volunteer")
@RequiredArgsConstructor
public class VolunteerController {

    private final Semaphore semaphore;
    private final VolunteerService volunteerService;

    @GetMapping()
    public ResponseEntity<List<VolunteerInfoResponseDto>> getVolunteerListWithInRadius(
            @Parameter(name = "경도", required = true) @RequestParam double latitude,
            @Parameter(name = "경도", required = true) @RequestParam double longitude,
            @Parameter(name = "반경", description = "단위 : m", required = true) @RequestParam int radius
    ) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerInfoResponseDto> getVolunteerWorkInfo(@PathVariable Long id) {
        try {
            VolunteerInfoResponseDto responseDto = volunteerService.getVolunteer(id);
            return ResponseEntity.ok(responseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "특정 기관 봉사활동 반환", description = "id 값을 받아 모든 봉사활동")
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<VolunteerInfoResponseDto>> getVolunteerListByOrganization(@PathVariable Long organizationId) {
        try {
            List<VolunteerInfoResponseDto> responseDtos = volunteerService.getAllVolunteersByOrganization(organizationId);
            return ResponseEntity.ok(responseDtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<UserInfoResponseDto>> getAllUsersByVolunteerWork(@PathVariable Long id) {
        try {
            List<UserInfoResponseDto> responseDtos = volunteerService.getAllUsersByVolunteerWork(id);
            return ResponseEntity.ok(responseDtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/join/{id}")
    public ResponseEntity<VolunteerInfoResponseDto> applyVolunteerWork(@PathVariable("id") Long id) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            volunteerService.apply(id, email);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | ConstraintViolationException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(408).build();

        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VolunteerInfoResponseDto> deleteVolunteerWork(@PathVariable("id") Long id) {
        try {
            volunteerService.delete(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<VolunteerInfoResponseDto> updateVolunteerWork(@PathVariable("id") Long id, @RequestBody VolunteerUpdateResponseDto updateResponseDto) {
        try {
            VolunteerInfoResponseDto responseDto = volunteerService.update(id, updateResponseDto);
            return ResponseEntity.ok(responseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "등록 대기 명단 확인", description = "status 에 따라 등록 상태 명단을 확인합니다.")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @GetMapping("/pending/{status}/{id}/organization")
    public ResponseEntity<List<UserInfoResponseDto>> getUserListForOrganization(
            @PathVariable Long id,
            @Parameter(description = "Pending, APPROVED, REJECT 를 받음.") @PathVariable ApplyStatus status) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            List<UserInfoResponseDto> infoResponseDtos = volunteerService.getUserListByStatusForOrganization(id, email, status);
            return ResponseEntity.ok(infoResponseDtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 어드민 역할을 가진 사람이 해당 기관의 등록 상태를 알 수 있음
     *
     * @param id     volunteerWorkId에 대한 정보.
     * @param status Pending, APPROVED, REJECT 를 받음.
     */

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/pending/{status}/{id}/admin")
    public ResponseEntity<List<UserInfoResponseDto>> getUserListForAdmin(@PathVariable Long id, @PathVariable ApplyStatus status) {
        try {
            List<UserInfoResponseDto> infoResponseDtos = volunteerService.getUserListByStatusForAdmin(id, status);
            return ResponseEntity.ok(infoResponseDtos);
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/approve/{userId}")
    public ResponseEntity<UserInfoResponseDto> approve(@PathVariable Long id, @PathVariable String userId) {
        try {
            volunteerService.approve(id, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "봉사활동 유저 취소", description = "기관 역할을 가진 사용자가 취소")
    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @PostMapping("/{id}/cancel/{userId}/organization")
    public ResponseEntity<UserInfoResponseDto> cancelForOrganization(
            @Parameter(description = "volunteerWorkId에 대한 정보", required = true) @PathVariable Long id,
            @Parameter(name = "유저 정보") @PathVariable String userId,
            @Parameter(name = "등록 상태", description = "등록 상태에 따라 유저가 추방되거나 거절됨 pending, approved 여야만 함") @RequestParam ApplyStatus status) {
        try {
            if (status == ApplyStatus.APPROVED) {
                volunteerService.cancelApprovedForOrganization(id, userId);
            }
            if (status == ApplyStatus.PENDING) {
                volunteerService.cancelPendingForOrganization(id, userId);
            }
            return ResponseEntity.ok().build();
        } catch (AuthorizationFailedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "봉사활동 유저 추방", description = "기관 역할을 가진 사용자가 추방")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/cancel/{userId}/admin")
    public ResponseEntity<UserInfoResponseDto> cancelForAdmin(
            @Parameter(description = "volunteerWorkId에 대한 정보", required = true) @PathVariable Long id,
            @Parameter(name = "유저 정보", required = true) @PathVariable String userId,
            @Parameter(name = "등록 상태", description = "등록 상태에 따라 유저가 추방되거나 거절됨 pending, approved 여야만 함", required = true) @RequestParam ApplyStatus status) {
        try {
            if (status == ApplyStatus.APPROVED) {
                volunteerService.cancelApproved(id, userId);
            }
            if (status == ApplyStatus.PENDING) {
                volunteerService.cancelPending(id, userId);
            }
            return ResponseEntity.ok().build();
        } catch (AuthorizationFailedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<UserInfoResponseDto> cancelForUser(@PathVariable Long id, @RequestParam ApplyStatus status) {
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (status == ApplyStatus.APPROVED) {
                volunteerService.cancelApproved(id, userId);
            }
            if (status == ApplyStatus.PENDING) {
                volunteerService.cancelPending(id, userId);
            }
            return ResponseEntity.ok().build();
        } catch (AuthorizationFailedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
