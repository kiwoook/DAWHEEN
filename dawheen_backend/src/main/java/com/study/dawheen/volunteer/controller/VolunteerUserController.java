package com.study.dawheen.volunteer.controller;

import com.study.dawheen.common.exception.AuthorizationFailedException;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.service.UserVolunteerService;
import com.study.dawheen.volunteer.service.VolunteerService;
import com.study.dawheen.volunteer.service.VolunteerUserQueryService;
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

@RestController
@Slf4j
@RequestMapping("/api/v1/volunteer_user")
@RequiredArgsConstructor
public class VolunteerUserController {

    private final VolunteerService volunteerService;
    private final UserVolunteerService userVolunteerService;
    private final VolunteerUserQueryService volunteerUserQueryService;

    @Operation(summary = "봉사활동 유저 리스트 ", description = "해당 volunteer 에 참여한 user info 를 반환합니다. ")
    @GetMapping("/users/{id}")
    public ResponseEntity<List<UserInfoResponseDto>> getAllUsersByVolunteerWork(@PathVariable Long id) {
        try {
            List<UserInfoResponseDto> responseDtos = volunteerUserQueryService.getAllUsersByVolunteerWork(id);
            return ResponseEntity.ok(responseDtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "봉사활동 유저 삭제", description = "봉사활동에서 유저를 삭제합니다. ")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserVolunteer(@PathVariable Long id) {
        try {
            userVolunteerService.deleteApprovedUserVolunteerWork(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "등록 대기 명단 확인", description = "status 에 따라 등록 상태 명단을 확인합니다.")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @GetMapping("/pending/{status}/{id}/organization")
    public ResponseEntity<List<UserInfoResponseDto>> getUserListForOrganization(
            @PathVariable Long id,
            @Parameter(description = "Pending, APPROVED, REJECT 를 받음.") @PathVariable ApplyStatus status) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            List<UserInfoResponseDto> infoResponseDtos = volunteerUserQueryService.getUserListByStatusForOrganization(id, email, status);
            return ResponseEntity.ok(infoResponseDtos);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "어드민 대기 명단 확인", description = "어드민이 특정 ID의 등록 상태 대기명단을 확인합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending/{status}/{id}/admin")
    public ResponseEntity<List<UserInfoResponseDto>> getUserListForAdmin(@PathVariable Long id, @PathVariable ApplyStatus status) {
        try {
            List<UserInfoResponseDto> infoResponseDtos = volunteerUserQueryService.getUserListByStatusForAdmin(id, status);
            return ResponseEntity.ok(infoResponseDtos);
        } catch (IllegalStateException | EntityNotFoundException e) {
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

    @Operation(summary = "봉사활동 유저 승인", description = "봉사활동을 승인합니다.")
    @PostMapping("/{id}/approve/{userId}")
    public ResponseEntity<UserInfoResponseDto> approve(@PathVariable Long id, @PathVariable Long userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            volunteerService.approve(email, id, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(summary = "봉사활동 유저 완료 처리", description = "봉사활동을 approved 된 유저를 completed 처리합니다.")
    @PostMapping("/{id}/complete/{userId}")
    public ResponseEntity<UserInfoResponseDto> complete(@PathVariable Long id, @PathVariable Long userId) {

        try {
            volunteerService.completed(id, userId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "봉사활동 유저 취소", description = "기관 역할을 가진 사용자가 취소")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @PostMapping("/{id}/cancel/{userId}/organization")
    public ResponseEntity<UserInfoResponseDto> cancelForOrganization(
            @Parameter(description = "volunteerWorkId에 대한 정보", required = true) @PathVariable Long id,
            @Parameter(name = "유저 아이디") @PathVariable Long userId,
            @Parameter(name = "등록 상태", description = "등록 상태에 따라 유저가 추방되거나 거절됨 pending, approved 여야만 함") @RequestParam ApplyStatus status) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            if (status == ApplyStatus.APPROVED) {
                volunteerService.cancelApprovedForOrganization(email, id, userId);
            }
            if (status == ApplyStatus.PENDING) {
                volunteerService.cancelPendingForOrganization(email, id, userId);
            }
            return ResponseEntity.ok().build();
        } catch (AuthorizationFailedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "봉사활동 유저 추방", description = "어드민 역할을 가진 사용자가 추방")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/cancel/{userId}/admin")
    public ResponseEntity<UserInfoResponseDto> cancelForAdmin(
            @Parameter(description = "volunteerWorkId에 대한 정보", required = true) @PathVariable Long id,
            @Parameter(name = "유저 아이디", required = true) @PathVariable Long userId,
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

    @Operation(summary = "봉사활동 유저 취소", description = "사용자가 해당 봉사활동을 취소합니다.")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<UserInfoResponseDto> cancelForUser(@PathVariable Long id, @RequestParam ApplyStatus status) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            if (status == ApplyStatus.APPROVED) {
                volunteerService.cancelApproved(id, email);
            }
            if (status == ApplyStatus.PENDING) {
                volunteerService.cancelPending(id, email);
            }
            return ResponseEntity.ok().build();
        } catch (AuthorizationFailedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
