package com.study.dawheen.infra.file.service;

import com.study.dawheen.infra.file.entity.FileEntity;
import com.study.dawheen.infra.file.repositoty.FileRepository;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${upload-path}")
    private static String path;

    private final FileRepository fileRepository;
    private final VolunteerWorkRepository volunteerWorkRepository;


    public Long saveImgFileByVolunteerWork(MultipartFile files, VolunteerWork volunteerWork) throws IOException {
        if (files.isEmpty()) {
            return null;
        }

        String contentType = files.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Uploaded file is not an image.");
        }

        String originalFilename = files.getOriginalFilename();

        if (originalFilename == null){
            return null;
        }

        // 파일 이름으로 쓸 uuid 생성
        String uuid = UUID.randomUUID().toString();

        // 확장자 추출(ex : .png)
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // uuid 와 확장자 결합
        String savedName = uuid + extension;

        // 파일을 불러올 때 사용할 파일 경로
        String savedPath = path + savedName;

        // 파일 엔티티 생성
        FileEntity file = FileEntity.builder()
                .originalName(originalFilename)
                .savedName(savedName)
                .savedPath(savedPath)
                .volunteerWork(volunteerWork)
                .build();

        // 실제로 로컬에 uuid 를 파일명으로 저장
        files.transferTo(new File(savedPath));

        // 데이터베이스에 파일 정보 저장
        FileEntity savedFile = fileRepository.save(file);

        return savedFile.getId();
    }

    public List<UrlResource> getImgUrlByVolunteerWorkId(Long volunteerWorkId) throws MalformedURLException {
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);
        List<String> filePathList = fileRepository.findAllByVolunteerWork(volunteerWork).stream().map(FileEntity::getSavedPath).toList();

        List<UrlResource> resourceList = new ArrayList<>();

        for (String filePath : filePathList){
            resourceList.add(new UrlResource("file:"+filePath));
        }

        return resourceList;
    }
}
