package com.study.dawheen.organization.entity;

import com.study.dawheen.common.entity.BaseTimeEntity;
import com.study.dawheen.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ORGANIZATION_SUBSCRIBE", uniqueConstraints = @UniqueConstraint(columnNames = {"ORGANIZATION_ID", "USER_ID"}))
public class OrganizationSubscribe extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder
    public OrganizationSubscribe(Organization organization, User user) {
        this.organization = organization;
        this.user = user;
    }
}
