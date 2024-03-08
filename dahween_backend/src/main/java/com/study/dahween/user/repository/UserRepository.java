package com.study.dahween.user.repository;

import com.study.dahween.organization.entity.Organization;
import com.study.dahween.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);

    // 해당 기관과 연결되어 있는 유저를 반환합니다.
    Optional<List<User>> findAllByOrganization(Organization organization);

}
