package com.study.dawheen.organization.repository;

import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.entity.OrganizationSubscribe;
import com.study.dawheen.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganSubscribeRepository extends JpaRepository<OrganizationSubscribe, Long>, OrganSubscribeRepositoryCustom {

    Optional<OrganizationSubscribe> findByUserAndOrganization(User user, Organization organization);

}
