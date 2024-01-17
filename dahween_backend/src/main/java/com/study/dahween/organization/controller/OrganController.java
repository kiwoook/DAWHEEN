package com.study.dahween.organization.controller;

import com.study.dahween.organization.dto.OrganInfoResponseDto;
import com.study.dahween.organization.dto.OrganRequestDto;
import com.study.dahween.organization.service.OrganService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
            return ResponseEntity.badRequest().build();
        }
    }

    // 기관 개설
    @PostMapping
    public ResponseEntity<OrganInfoResponseDto> createOrganization(@RequestBody OrganRequestDto requestDto){
        try{
            OrganInfoResponseDto responseDto = organService.create(requestDto);
            return ResponseEntity.ok(responseDto);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }


    // TODO 기관 수정과 삭제는 어드민과 해당 게시글 작성자만 할 수 있도록 해야함.
    // 기관 수정

    // 기관 삭제

    // TODO 해당 기관의 후기 모음

}
