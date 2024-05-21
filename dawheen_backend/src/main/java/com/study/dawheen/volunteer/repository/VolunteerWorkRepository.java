package com.study.dawheen.volunteer.repository;

import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerWorkRepository extends JpaRepository<VolunteerWork, Long>, VolunteerWorkRepositoryCustom {

    List<VolunteerWork> getAllByOrganization(Organization organization);

}
