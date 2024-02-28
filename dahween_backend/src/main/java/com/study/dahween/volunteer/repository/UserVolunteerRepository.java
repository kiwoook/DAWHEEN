package com.study.dahween.volunteer.repository;

import com.study.dahween.volunteer.entity.UserVolunteerWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserVolunteerRepository extends JpaRepository<UserVolunteerWork, Long>, UserVolunteerRepositoryCustom {
    Optional<List<UserVolunteerWork>> findAllByVolunteerWorkId(Long volunteerWorkId);


}
