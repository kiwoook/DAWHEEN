package com.study.dahween.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.dahween.auth.entity.ProviderType;
import com.study.dahween.auth.utils.PasswordUtil;
import com.study.dahween.common.entity.Address;
import com.study.dahween.common.entity.BaseTimeEntity;
import com.study.dahween.organization.entity.Organization;
import com.study.dahween.volunteer.entity.UserVolunteerWork;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "SOCIAL_ID")
    private String socialId;

    @JsonIgnore
    @Column(name = "PASSWORD", length = 128)
    @NotNull
    @Size(max = 128)
    private String password;

    @Column()
    private String name;


    @Column
    private String phone;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    private Address address;

    @Column
    @Enumerated(EnumType.STRING)
    private RoleType role;

    private String refreshToken;

    @Column(name = "PROVIDER_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserVolunteerWork> volunteerWorks = new HashSet<>();

    @Builder
    public User(String socialId, String password, String name, String email, ProviderType providerType, RoleType roleType) {
        this.socialId = socialId;
        this.password = passwordEncode(password);
        this.name = name;
        this.email = email;
        this.providerType = providerType;
        this.role = roleType;
    }

    public User updateSocialId(String socialId) {
        this.socialId = socialId;
        return this;
    }

    public User update(String name, String email, String phone, Address address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;

        return this;
    }

    public void verifyOAuth2User(){
        this.role = RoleType.MEMBER;
    }

    public void grantAdmin() {
        this.role = RoleType.ADMIN;
    }

    public void grantOrganization(Organization organization) {
        this.role = RoleType.ORGANIZATION;
        this.organization = organization;
        organization.addUser(this);
    }

    public void revokeOrganization() {
        this.role = RoleType.MEMBER;
        this.organization = null;
    }

    public void associateOrganization(Organization organization) {
        this.organization = organization;
    }

    public void attendVolunteerWork(UserVolunteerWork userVolunteerWork) {
        this.volunteerWorks.add(userVolunteerWork);
        userVolunteerWork.updateStatus(ApplyStatus.APPROVED);
    }

    public void leaveVolunteerWork(UserVolunteerWork userVolunteerWork) {
        this.volunteerWorks.remove(userVolunteerWork);
    }

    private String passwordEncode(String rawPassword) {
        if (rawPassword == null) {
            return PasswordUtil.generateRandomPassword();
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(rawPassword);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }


}
