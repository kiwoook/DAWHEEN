package com.study.dawheen.volunteer.repository;

import com.study.dawheen.user.entity.User;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;

import java.util.List;
import java.util.Optional;

public interface UserVolunteerRepositoryCustom {

    boolean existsByVolunteerWorkAndUserIdAndStatus(Long volunteerWorkId, Long userId, List<ApplyStatus> statuses);

    boolean existsByVolunteerWorkAndEmailAndStatus(Long volunteerWorkId, String email, List<ApplyStatus> statuses);

    Optional<List<UserVolunteerWork>> findAllByVolunteerWorkIdWithFetch(Long volunteerWorkId);

    List<User> findUsersByVolunteerWorkIdAndStatus(Long volunteerWorkId, ApplyStatus status);

    Optional<List<User>> findUsersByVolunteerWorkId(Long volunteerWorkId);

    Optional<UserVolunteerWork> findByVolunteerWorkIdAndUserId(Long volunteerWorkId, Long userId);

    Optional<UserVolunteerWork> findByVolunteerWorkIdAndEmail(Long volunteerWorkId, String email);

}
