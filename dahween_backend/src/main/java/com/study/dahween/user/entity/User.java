package com.study.dahween.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.dahween.common.entity.Address;
import com.study.dahween.common.entity.BaseTimeEntity;
import com.study.dahween.oauth.entity.ProviderType;
import com.study.dahween.organization.entity.Organization;
import com.study.dahween.volunteer.entity.UserVolunteerWork;
import com.study.dahween.volunteer.entity.VolunteerWork;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String userId;

    @JsonIgnore
    @Column(name = "PASSWORD", length = 128)
    @NotNull
    @Size(max = 128)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Embedded
    @Column
    private Address address;

    @Embedded
    @Column
    private Role role;


    @Column(name = "PROVIDER_TYPE", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserVolunteerWork> volunteerWorks = new HashSet<>();

    @Builder
    public User(String userId, String name, String email, ProviderType providerType) {
        this.userId = userId;
        this.password = "NO_PASSWORD";
        this.name = name;
        this.email = email;
        this.role = new Role(RoleType.MEMBER);
        this.providerType = providerType;
    }

    public User updateUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public User update(String name, String email, String phone, Address address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;

        return this;
    }


    public void grantAdmin() {
        this.role = new Role(RoleType.ADMIN);
    }

    public void grantOrganization(Organization organization) {
        this.role = new Role(RoleType.ORGANIZATION);
        this.organization = organization;
        organization.addUser(this);
    }

    public void revokeOrganization(){
        this.role = new Role(RoleType.MEMBER);
        this.organization = null;
    }

    public void associateOrganization(Organization organization) {
        this.organization = organization;
    }

    public void attendVolunteerWork(UserVolunteerWork userVolunteerWork){
        this.volunteerWorks.add(userVolunteerWork);
        userVolunteerWork.updateStatus(ApplyStatus.APPROVED);
    }

    public void leaveVolunteerWork(UserVolunteerWork userVolunteerWork){
        this.volunteerWorks.remove(userVolunteerWork);
    }


}
