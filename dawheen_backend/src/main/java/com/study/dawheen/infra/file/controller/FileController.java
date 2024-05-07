package com.study.dawheen.infra.file.controller;

import com.study.dawheen.infra.file.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.List;

@Tag(name = "파일 관련 API")
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/image/volunteer/{volunteerWorkId}")
    public ResponseEntity<List<UrlResource>> downloadVolunteerImage(@PathVariable Long volunteerWorkId) throws MalformedURLException {
        List<UrlResource> resourceList = fileService.getImgUrlByVolunteerWorkId(volunteerWorkId);

        return ResponseEntity.ok(resourceList);
    }

}
