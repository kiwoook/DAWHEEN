package com.study.dawheen.volunteer.repository;

import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserVolunteerRepository extends JpaRepository<UserVolunteerWork, Long>,
        UserVolunteerRepositoryCustom,
        UserVolunteerWorkRankingRepositoryCustom {

    List<UserVolunteerWork> findAllByVolunteerWorkId(Long volunteerWorkId);

}
