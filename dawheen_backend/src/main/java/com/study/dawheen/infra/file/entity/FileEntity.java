package com.study.dawheen.infra.file.entity;

import com.study.dawheen.volunteer.entity.VolunteerWork;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "FILE")
@Getter
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, name = "ORIGINAL_NAME")
    private String originalName;

    @Column(nullable = false, name = "SAVED_NAME")
    private String savedName;

    @Column(nullable = false, name = "SAVED_PATH")
    private String savedPath;

    @ManyToOne(fetch = FetchType.LAZY)
    private VolunteerWork volunteerWork;

    @Builder
    public FileEntity(String originalName, String savedName, String savedPath, VolunteerWork volunteerWork) {
        this.originalName = originalName;
        this.savedName = savedName;
        this.savedPath = savedPath;
        this.volunteerWork = volunteerWork;
    }
}
