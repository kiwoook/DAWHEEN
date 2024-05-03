package com.study.dawheen.organization.repository;

import com.study.dawheen.user.dto.UserInfoResponseDto;

import java.util.List;

public interface OrganSubscribeRepositoryCustom {

    List<UserInfoResponseDto> findUserByOrganizationId(Long id);
}
