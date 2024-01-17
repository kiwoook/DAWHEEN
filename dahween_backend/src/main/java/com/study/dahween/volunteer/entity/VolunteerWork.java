package com.study.dahween.volunteer.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "VOLUNTEER_WORK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TODO 자원봉사장인 유저와 기관을 연결하는 엔티티
public class VolunteerWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

}
