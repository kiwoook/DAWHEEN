package com.study.dawheen.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.dawheen.auth.entity.ProviderType;
import com.study.dawheen.common.entity.Address;
import com.study.dawheen.common.entity.BaseTimeEntity;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "USERS")
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
    @Size(min = 60, max = 60)
    private String password; // BCryptPasswordEncoder 사용 시 60자의 길이를 가지고 있음

    private String birth;

    @NotNull
    @Column
    private String name;

    @Column
    private String phone;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    private Address address;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(name = "PROVIDER_TYPE", length = 20)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserVolunteerWork> volunteerWorks = new HashSet<>();

    @Version
    private Long version;

    @Builder
    public User(String socialId, String password, String name, String email, String birth, ProviderType providerType, RoleType roleType) {
        this.socialId = socialId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.birth = birth;
        this.providerType = providerType;
        this.role = roleType;
    }


    public void changePassword(String password) {
        this.password = password;
    }

    public User updateSocialId(String socialId) {
        this.socialId = socialId;
        return this;
    }

    public void update(String name, String email, String phone, Address address) {
        this.name = name;
        if (email != null) {
            this.email = email;
        }
        this.phone = phone;
        this.address = address;

    }

    public void verifyOAuth2User() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getSocialId(), user.getSocialId()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getBirth(), user.getBirth()) && Objects.equals(getName(), user.getName()) && Objects.equals(getPhone(), user.getPhone()) && Objects.equals(getAddress(), user.getAddress()) && getRole() == user.getRole() && getProviderType() == user.getProviderType() && Objects.equals(getOrganization(), user.getOrganization()) && Objects.equals(getVolunteerWorks(), user.getVolunteerWorks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getSocialId(), getPassword(), getBirth(), getName(), getPhone(), getAddress(), getRole(), getProviderType(), getOrganization(), getVolunteerWorks());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", socialId='" + socialId + '\'' +
                ", password='" + password + '\'' +
                ", birth='" + birth + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address=" + address +
                ", role=" + role +
                ", providerType=" + providerType +
                ", organization=" + organization +
                ", volunteerWorks=" + volunteerWorks +
                '}';
    }
}
