package com.study.dawheen.volunteer.entity;

import com.study.dawheen.common.entity.BaseTimeEntity;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@Table(name = "USER_VOLUNTEERWORK", indexes = @Index(name = "IDX_USER_ID_AND_STATUS", columnList = "USER_ID, STATUS"), uniqueConstraints = @UniqueConstraint(columnNames = {"VOLUNTEER_WORK_ID", "USER_ID"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserVolunteerWork extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "email")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VOLUNTEER_WORK_ID")
    private VolunteerWork volunteerWork;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

    @Version
    private Long version;


    public UserVolunteerWork(User user, VolunteerWork volunteerWork) {
        this.user = user;
        this.volunteerWork = volunteerWork;
        this.status = ApplyStatus.PENDING;
    }

    public void updateStatus(ApplyStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserVolunteerWork{" +
                "id=" + id +
                ", user=" + user +
                ", volunteerWork=" + volunteerWork +
                ", status=" + status +
                '}';
    }

}
