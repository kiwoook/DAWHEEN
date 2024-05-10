package com.study.dawheen.infra.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.study.dawheen.infra.file.entity.FileEntity;
import com.study.dawheen.infra.file.repositoty.FileRepository;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${upload-path}")
    private static String path;
    private final FileRepository fileRepository;
    private final VolunteerWorkRepository volunteerWorkRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String s3Bucket;

    public void saveImgFileByVolunteerWork(MultipartFile file, VolunteerWork volunteerWork) throws IOException {
        String originalFileName = file.getOriginalFilename();
        log.info("파일명 = {}", originalFileName);

        if (file.isEmpty() || originalFileName == null) {
            throw new IllegalArgumentException();
        }

        // 이미지 파일 확인
        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Uploaded file is not an image.");
        }
        String savedName = uuidFileName(originalFileName);


        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try(InputStream inputStream = file.getInputStream()){
            amazonS3Client.putObject(new PutObjectRequest(s3Bucket, savedName, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        }

        String s3Path = amazonS3Client.getUrl(s3Bucket, savedName).toString();

        FileEntity fileEntity = FileEntity.builder()
                .originalName(originalFileName)
                .savedName(savedName)
                .savedPath(s3Path)
                .volunteerWork(volunteerWork)
                .build();

        fileRepository.save(fileEntity);
    }

    public void deleteImgFile(String fileName){
        amazonS3Client.deleteObject(s3Bucket, fileName);
    }


    public List<UrlResource> getImgUrlByVolunteerWorkId(Long volunteerWorkId) throws MalformedURLException {
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);
        List<String> filePathList = fileRepository.findAllByVolunteerWork(volunteerWork).stream().map(FileEntity::getSavedPath).toList();

        List<UrlResource> resourceList = new ArrayList<>();

        for (String filePath : filePathList) {
            resourceList.add(new UrlResource("file:" + filePath));
        }

        return resourceList;
    }


    private String uuidFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        return uuid + extension;
    }


}
