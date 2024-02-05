package com.study.dahween.user.entity;

import com.study.dahween.organization.entity.Organization;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Enumerated(EnumType.STRING)
    private RoleType roleType;


    public Role(RoleType roleType) {
        this.roleType = roleType;
    }


}
