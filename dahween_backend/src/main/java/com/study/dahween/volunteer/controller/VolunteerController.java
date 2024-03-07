package com.study.dahween.volunteer.controller;

import com.study.dahween.common.exception.AuthorizationFailedException;
import com.study.dahween.user.dto.UserInfoResponseDto;
import com.study.dahween.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dahween.volunteer.dto.VolunteerUpdateResponseDto;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import com.study.dahween.volunteer.service.VolunteerService;
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

    /**
     * 특정 기관과 관련된 봉사활동 반환
     */

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
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            volunteerService.apply(id, userId);
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

    /**
     * 기관역할을 가진 사람이 해당 기관의 등록 상태를 알 수 있음
     *
     * @param status Pending, APPROVED, REJECT 를 받음.
     */

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @GetMapping("/pending/{status}/{id}/organization")
    public ResponseEntity<List<UserInfoResponseDto>> getUserListForOrganization(@PathVariable Long id, @PathVariable ApplyStatus status) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            List<UserInfoResponseDto> infoResponseDtos = volunteerService.getUserListByStatusForOrganization(id, userId, status);
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

    /**
     * Organization 역할을 가진 사용자가 추방함
     *
     * @param id     volunteerWorkId에 대한 정보.
     * @param status Pending, APPROVED 를 받음.
     */

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @PostMapping("/{id}/cancel/{userId}/organization")
    public ResponseEntity<UserInfoResponseDto> cancelForOrganization(@PathVariable Long id, @PathVariable String userId, @RequestParam ApplyStatus status) {
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/cancel/{userId}/admin")
    public ResponseEntity<UserInfoResponseDto> cancelForAdmin(@PathVariable Long id, @PathVariable String userId, @RequestParam ApplyStatus status) {
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
