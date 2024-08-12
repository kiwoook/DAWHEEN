package com.study.dawheen.volunteer.entity;

import com.study.dawheen.chat.entity.ChatRoom;
import com.study.dawheen.common.dto.CoordinateDto;
import com.study.dawheen.common.entity.BaseTimeEntity;
import com.study.dawheen.common.entity.Coordinate;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.volunteer.dto.VolunteerCreateRequestDto;
import com.study.dawheen.volunteer.dto.VolunteerUpdateRequestDto;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


@Entity
@Getter
@Table(name = "VOLUNTEER_WORK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VolunteerWork extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "volunteerWork")
    private Set<UserVolunteerWork> users = new HashSet<>();

    private String title;

    @Lob
    private String content;

    @Column(name = "SERVICE_START_DATE")
    private LocalDate serviceStartDate;

    @Column(name = "SERVICE_END_DATE")
    private LocalDate serviceEndDate;

    @Column(name = "SERVICE_START_TIME")
    private LocalTime serviceStartTime;

    @Column(name = "SERVICE_END_TIME")
    private LocalTime serviceEndTime;

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "SERVICE_DAYS_OF_WEEK", joinColumns =
    @JoinColumn(name = "VOLUNTEER_WORK_ID")
    )
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> serviceDays;

    @ElementCollection(targetClass = TargetAudience.class)
    @CollectionTable(name = "TARGET_AUDIENCE", joinColumns =
    @JoinColumn(name = "VOLUNTEER_WORK_ID"))
    @Enumerated(EnumType.STRING)
    private Set<TargetAudience> targetAudiences;


    @ElementCollection(targetClass = VolunteerType.class)
    @CollectionTable(name = "VOLUNTEER_TYPE", joinColumns =
    @JoinColumn(name = "VOLUNTEER_WORK_ID"))
    @Enumerated(EnumType.STRING)
    private Set<VolunteerType> volunteerTypes;

    @Column(name = "RECRUIT_START_DATETIME")
    private LocalDateTime recruitStartDateTime;

    @Column(name = "RECRUIT_END_DATETIME")
    private LocalDateTime recruitEndDateTime;

    @Column(name = "APPLIED_PARTICIPANTS")
    private AtomicInteger appliedParticipants;
    @Column(name = "MAX_PARTICIPANTS")
    private Integer maxParticipants;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "COORDINATE_ID")
    private Coordinate coordinate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "CHATROOM_ID")
    private ChatRoom chatRoom;

    @Builder
    public VolunteerWork(Organization organization, String title, String content, LocalDate serviceStartDate, LocalDate serviceEndDate, LocalTime serviceStartTime, LocalTime serviceEndTime, Set<DayOfWeek> serviceDays, Set<TargetAudience> targetAudiences, Set<VolunteerType> volunteerTypes, LocalDateTime recruitStartDateTime, LocalDateTime recruitEndDateTime, int maxParticipants) {
        this.organization = organization;
        this.title = title;
        this.content = content;
        this.serviceStartDate = serviceStartDate;
        this.serviceEndDate = serviceEndDate;
        this.serviceStartTime = serviceStartTime;
        this.serviceEndTime = serviceEndTime;
        this.serviceDays = serviceDays;
        this.targetAudiences = targetAudiences;
        this.volunteerTypes = volunteerTypes;
        this.recruitStartDateTime = recruitStartDateTime;
        this.recruitEndDateTime = recruitEndDateTime;
        this.maxParticipants = maxParticipants;
        this.appliedParticipants = new AtomicInteger(0);
        this.chatRoom = new ChatRoom(title);
    }

    public static VolunteerWork toEntity(VolunteerCreateRequestDto requestDto) {
        return VolunteerWork.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .serviceStartDate(requestDto.getServiceStartDate())
                .serviceEndDate(requestDto.getServiceEndDate())
                .serviceStartTime(requestDto.getServiceStartTime())
                .serviceEndTime(requestDto.getServiceEndTime())
                .serviceDays(requestDto.getServiceDays())
                .recruitStartDateTime(requestDto.getRecruitStartDateTime())
                .recruitEndDateTime(requestDto.getRecruitEndDateTime())
                .maxParticipants(requestDto.getMaxParticipants())
                .targetAudiences(requestDto.getTargetAudiences())
                .volunteerTypes(requestDto.getVolunteerTypes())
                .build();
    }

    public void update(VolunteerUpdateRequestDto volunteerUpdateRequestDto) {
        this.title = volunteerUpdateRequestDto.getTitle();
        this.content = volunteerUpdateRequestDto.getContent();
        this.serviceDays = volunteerUpdateRequestDto.getServiceDays();
        this.serviceStartTime = volunteerUpdateRequestDto.getServiceStartTime();
        this.serviceEndTime = volunteerUpdateRequestDto.getServiceEndTime();
        this.serviceStartDate = volunteerUpdateRequestDto.getServiceStartDate();
        this.serviceEndDate = volunteerUpdateRequestDto.getServiceEndDate();
        this.recruitStartDateTime = volunteerUpdateRequestDto.getRecruitStartDateTime();
        this.recruitEndDateTime = volunteerUpdateRequestDto.getRecruitEndDateTime();
        this.maxParticipants = volunteerUpdateRequestDto.getMaxParticipants();
        this.volunteerTypes = volunteerUpdateRequestDto.getVolunteerTypes();
        this.targetAudiences = volunteerUpdateRequestDto.getTargetAudiences();
        this.coordinate = new Coordinate(volunteerUpdateRequestDto.getLatitude(), volunteerUpdateRequestDto.getLongitude());
    }

    public void updateCoordinate(CoordinateDto coordinateDto) {
        this.coordinate = new Coordinate(coordinateDto.getLatitude(), coordinateDto.getLongitude());
    }

    public void updateOrganization(Organization organization) {
        this.organization = organization;
    }

    public int increaseParticipants() {
        return appliedParticipants.incrementAndGet();
    }

    public int decreaseParticipants() throws IllegalStateException {
        if (appliedParticipants.get() <= 0) {
            throw new IllegalStateException();
        }
        return appliedParticipants.decrementAndGet();
    }

    public void attendUser(UserVolunteerWork userVolunteerWork) {
        users.add(userVolunteerWork);
    }

    public void leaveUser(UserVolunteerWork userVolunteerWork) {
        users.remove(userVolunteerWork);
    }

}
