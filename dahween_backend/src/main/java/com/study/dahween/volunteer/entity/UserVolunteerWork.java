package com.study.dahween.volunteer.entity;

import com.study.dahween.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@Table(name = "USER_VOLUNTEERWORK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserVolunteerWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "volunteer_work_id")
    private VolunteerWork volunteerWork;

    public UserVolunteerWork(User user, VolunteerWork volunteerWork) {
        this.user = user;
        this.volunteerWork = volunteerWork;
    }
}
