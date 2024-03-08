package com.study.dahween.volunteer.entity;

import com.study.dahween.common.entity.BaseTimeEntity;
import com.study.dahween.user.entity.User;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@Table(name = "USER_VOLUNTEERWORK", uniqueConstraints = @UniqueConstraint(columnNames = {"VOLUNTEER_WORK_ID", "USER_ID"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserVolunteerWork extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VOLUNTEER_WORK_ID")
    private VolunteerWork volunteerWork;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

    public UserVolunteerWork(User user, VolunteerWork volunteerWork) {
        this.user = user;
        this.volunteerWork = volunteerWork;
        status = ApplyStatus.PENDING;
    }

    public void updateStatus(ApplyStatus status) {
        this.status = status;
    }
}
