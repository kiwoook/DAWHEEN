package com.study.dahween.organization.repository;

import com.study.dahween.organization.entity.Organization;
import com.study.dahween.volunteer.entity.type.TargetAudience;
import com.study.dahween.volunteer.entity.type.VolunteerType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrganRepositoryCustom {

    Optional<List<Organization>> findOrganizationsWithinRadius(double latitude, double longitude, int radius);



}
