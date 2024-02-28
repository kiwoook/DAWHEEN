package com.study.dahween.organization.repository;

import com.study.dahween.organization.entity.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganRepositoryCustom {

    Optional<List<Organization>> findOrganizationsWithinRadius(double latitude, double longitude, int radius);
}
