package com.study.dahween.volunteer.repository;

import com.study.dahween.organization.entity.Organization;
import com.study.dahween.volunteer.entity.VolunteerWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VolunteerWorkRepository extends JpaRepository<VolunteerWork, Long> {

    Optional<List<VolunteerWork>> getAllByOrganization(Organization organization);
}
