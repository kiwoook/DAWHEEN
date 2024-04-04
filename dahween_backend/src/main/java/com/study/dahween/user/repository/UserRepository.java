package com.study.dahween.user.repository;

import com.study.dahween.auth.entity.ProviderType;
import com.study.dahween.organization.entity.Organization;
import com.study.dahween.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmailAndName(String email, String name);

    boolean existsByEmail(String email);

    // 해당 기관과 연결되어 있는 유저를 반환합니다.
    Optional<List<User>> findAllByOrganization(Organization organization);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findBySocialIdAndProviderType(String socialId, ProviderType providerType);



}
