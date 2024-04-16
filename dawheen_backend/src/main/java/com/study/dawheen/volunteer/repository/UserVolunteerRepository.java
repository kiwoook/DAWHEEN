package com.study.dawheen.volunteer.repository;

import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserVolunteerRepository extends JpaRepository<UserVolunteerWork, Long>,
        UserVolunteerRepositoryCustom,
        UserVolunteerWorkRankingRepositoryCustom {

    Optional<List<UserVolunteerWork>> findAllByVolunteerWorkId(Long volunteerWorkId);

}
