package com.study.dahween.organization.repository;

import com.study.dahween.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganRepository extends JpaRepository<Organization, Long> {


}
