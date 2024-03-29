package com.study.dahween.volunteer.controller;

import com.study.dahween.volunteer.service.UserVolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/user_volunteer")
@RequiredArgsConstructor
public class UserVolunteerController {

    private final UserVolunteerService userVolunteerService;

    @Operation(summary = "봉사활동 유저 삭제", description = "봉사활동에서 유저를 삭제합니다. ")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserVolunteer(@PathVariable Long id) {
        try {
            userVolunteerService.deleteUserVolunteerWork(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
