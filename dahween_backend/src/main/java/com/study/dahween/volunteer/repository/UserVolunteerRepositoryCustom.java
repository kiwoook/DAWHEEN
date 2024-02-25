package com.study.dahween.volunteer.repository;

import com.study.dahween.user.entity.User;
import com.study.dahween.volunteer.entity.UserVolunteerWork;
import com.study.dahween.volunteer.entity.VolunteerWork;
import com.study.dahween.volunteer.entity.type.ApplyStatus;

import java.util.List;
import java.util.Optional;

public interface UserVolunteerRepositoryCustom {

    boolean existsByVolunteerWorkAndUserAndStatus(Long volunteerWorkId, String userId, List<ApplyStatus> statuses);

    Optional<List<UserVolunteerWork>> findAllByVolunteerWorkIdWithFetch(Long volunteerWorkId);

    List<User> findUsersByVolunteerWorkIdAndStatus(Long volunteerWorkId, ApplyStatus status);

    Optional<List<User>> findUsersByVolunteerWorkId(Long volunteerWorkId);

    Optional<UserVolunteerWork> findByVolunteerWorkIdAndUserId(Long volunteerWorkId, String userId);


    // 봉사활동 ID와 STATUS 로 개수 찾기
    int countAllByVolunteerWorkIdAndStatus(Long volunteerWorkId, ApplyStatus status);

}
