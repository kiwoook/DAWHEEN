package com.study.dawheen.user.repository;

import com.study.dawheen.auth.entity.ProviderType;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<User> findByEmail(String email);

    boolean existsByEmailAndName(String email, String name);

    boolean existsByEmail(String email);

    // 해당 기관과 연결되어 있는 유저를 반환합니다.
    Optional<List<User>> findAllByOrganization(Organization organization);

    Optional<User> findBySocialIdAndProviderType(String socialId, ProviderType providerType);


}
