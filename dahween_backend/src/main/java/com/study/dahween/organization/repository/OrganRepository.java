package com.study.dahween.organization.repository;

import com.study.dahween.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganRepository extends JpaRepository<Organization, Long>, OrganRepositoryCustom {
    Optional<List<Organization>> getAllByApproved(boolean bool);
}
