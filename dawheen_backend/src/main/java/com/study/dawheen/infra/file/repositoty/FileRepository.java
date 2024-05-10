package com.study.dawheen.infra.file.repositoty;

import com.study.dawheen.infra.file.entity.FileEntity;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findAllByVolunteerWork(VolunteerWork volunteerWork);

}
