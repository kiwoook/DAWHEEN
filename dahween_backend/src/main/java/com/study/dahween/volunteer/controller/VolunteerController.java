package com.study.dahween.volunteer.controller;

import com.study.dahween.volunteer.service.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Semaphore;

@RestController
@Slf4j
@RequestMapping("/api/v1/volunteer")
@RequiredArgsConstructor
public class VolunteerController {

    private final Semaphore semaphore;
    private final VolunteerService volunteerService;

    @PostMapping("/join/{id}")
    public ResponseEntity<?> applyVolunteer(@PathVariable("id") Long id) {
        try{
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            volunteerService.apply(id, userId);
            return ResponseEntity.ok().build();
        }catch (EntityNotFoundException | ConstraintViolationException e){
            return ResponseEntity.badRequest().build();
        }catch (RuntimeException e){
            return ResponseEntity.internalServerError().build();
        }
    }

}
