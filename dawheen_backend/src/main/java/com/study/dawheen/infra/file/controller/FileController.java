package com.study.dawheen.infra.file.controller;

import com.study.dawheen.infra.file.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping("/upload/test")
    public ResponseEntity<?> uploadTest(@RequestParam(value = "file", required = false) MultipartFile file,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files) throws IOException {
        fileService.saveImgFileByVolunteerWork(file, null);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteFile(@RequestBody String fileName) {
        fileService.deleteImgFile(fileName);

        return ResponseEntity.ok().build();
    }

}
