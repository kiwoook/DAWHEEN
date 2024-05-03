package com.study.dawheen.infra.file.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileUploadResponseDto implements Serializable {

    private String fileName;
    private String uuid;
    private String folderPath;

    public String getImageUrl() {
        return URLEncoder.encode(this.folderPath + "/" + this.uuid + "_" + this.fileName, StandardCharsets.UTF_8);
    }
}
