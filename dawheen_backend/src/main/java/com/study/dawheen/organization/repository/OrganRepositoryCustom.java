package com.study.dawheen.organization.repository;

import com.study.dawheen.organization.entity.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganRepositoryCustom {

    Optional<List<Organization>> findOrganizationsWithinRadius(double latitude, double longitude, int radius);

}
