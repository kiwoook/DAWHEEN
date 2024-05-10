package com.study.dawheen.volunteer.controller;

import com.study.dawheen.volunteer.dto.VolunteerCreateRequestDto;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.dto.VolunteerUpdateResponseDto;
import com.study.dawheen.volunteer.service.VolunteerQueryService;
import com.study.dawheen.volunteer.service.VolunteerService;
import com.study.dawheen.volunteer.service.VolunteerUserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "봉사활동", description = "봉사활동 관련 API")
@RestController
@Slf4j
@RequestMapping("/api/v1/volunteer")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final VolunteerQueryService volunteerQueryService;
    private final VolunteerUserQueryService volunteerUserQueryService;

    @GetMapping()
    public ResponseEntity<List<VolunteerInfoResponseDto>> getVolunteerListWithInRadius(
            @Parameter(name = "경도", required = true) @RequestParam double latitude,
            @Parameter(name = "경도", required = true) @RequestParam double longitude,
            @Parameter(name = "반경", description = "단위 : m", required = true) @RequestParam int radius
    ) {
        try {
            List<VolunteerInfoResponseDto> responseDtos = volunteerQueryService.getVolunteersWithinRadius(latitude, longitude, radius);
            return ResponseEntity.ok(responseDtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<VolunteerInfoResponseDto> createVolunteer(
            @RequestParam("file") MultipartFile file,
            @RequestParam("files") List<MultipartFile> files,
            @RequestBody @Valid VolunteerCreateRequestDto requestDto
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            VolunteerInfoResponseDto responseDto = volunteerService.create(email, requestDto, file, files);
            return ResponseEntity.ok(responseDto);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerInfoResponseDto> getVolunteerWorkInfo(@PathVariable Long id) {
        try {
            VolunteerInfoResponseDto responseDto = volunteerQueryService.getVolunteer(id);
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
            List<VolunteerInfoResponseDto> responseDtos = volunteerQueryService.getAllVolunteersByOrganization(organizationId);
            return ResponseEntity.ok(responseDtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
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

}
