package com.study.dawheen.volunteer.entity;

import com.study.dawheen.chat.entity.ChatRoom;
import com.study.dawheen.common.dto.CoordinateDto;
import com.study.dawheen.common.entity.BaseTimeEntity;
import com.study.dawheen.common.entity.Coordinate;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.volunteer.dto.VolunteerCreateRequestDto;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Organization organization;

    @OneToMany(mappedBy = "volunteerWork")
    private Set<UserVolunteerWork> users = new HashSet<>();

    @NotNull
    private String title;

    @Lob
    private String content;

    @NotNull
    @Column(name = "SERVICE_START_DATETIME")
    private LocalDateTime serviceStartDatetime;

    @NotNull
    @Column(name = "SERVICE_END_DATETIME")
    private LocalDateTime serviceEndDatetime;

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
    public VolunteerWork(Organization organization, String title, String content, LocalDateTime serviceStartDatetime, LocalDateTime serviceEndDatetime, Set<DayOfWeek> serviceDays, Set<TargetAudience> targetAudiences, Set<VolunteerType> volunteerTypes, LocalDateTime recruitStartDateTime, LocalDateTime recruitEndDateTime, int maxParticipants) {
        this.organization = organization;
        this.title = title;
        this.content = content;
        this.serviceStartDatetime = serviceStartDatetime;
        this.serviceEndDatetime = serviceEndDatetime;
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
                .serviceStartDatetime(requestDto.getServiceStartDatetime())
                .serviceEndDatetime(requestDto.getServiceEndDatetime())
                .serviceDays(requestDto.getServiceDays())
                .recruitStartDateTime(requestDto.getRecruitStartDateTime())
                .recruitEndDateTime(requestDto.getRecruitEndDateTime())
                .maxParticipants(requestDto.getMaxParticipants())
                .targetAudiences(requestDto.getTargetAudiences())
                .volunteerTypes(requestDto.getVolunteerTypes())
                .build();
    }

    public void update(
            String title,
            String content,
            LocalDateTime serviceStartDatetime,
            LocalDateTime serviceEndDatetime,
            Set<DayOfWeek> serviceDays,
            Set<TargetAudience> targetAudiences,
            Set<VolunteerType> volunteerTypes,
            LocalDateTime recruitStartDateTime,
            LocalDateTime recruitEndDateTime,
            Integer maxParticipants,
            Double latitude, // Assuming latitude and longitude are represented as Double
            Double longitude
    ) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (serviceStartDatetime != null) {
            this.serviceStartDatetime = serviceStartDatetime;
        }
        if (serviceEndDatetime != null) {
            this.serviceEndDatetime = serviceEndDatetime;
        }
        if (serviceDays != null) {
            this.serviceDays = serviceDays;
        }
        if (targetAudiences != null) {
            this.targetAudiences = targetAudiences;
        }
        if (volunteerTypes != null) {
            this.volunteerTypes = volunteerTypes;
        }
        if (recruitStartDateTime != null) {
            this.recruitStartDateTime = recruitStartDateTime;
        }
        if (recruitEndDateTime != null) {
            this.recruitEndDateTime = recruitEndDateTime;
        }
        if (maxParticipants != null) {
            this.maxParticipants = maxParticipants;
        }
        if (latitude != null && longitude != null && (this.coordinate == null ||
                !this.coordinate.getLatitude().equals(latitude) ||
                !this.coordinate.getLongitude().equals(longitude))) {
            this.coordinate = new Coordinate(latitude, longitude);
        }

    }

    public void updateCoordinate(CoordinateDto coordinateDto) {
        if (this.coordinate == null ||
                !this.coordinate.getLatitude().equals(coordinateDto.getLatitude()) ||
                !this.coordinate.getLongitude().equals(coordinateDto.getLongitude())) {
            this.coordinate = new Coordinate(coordinateDto.getLatitude(), coordinateDto.getLongitude());
        }
    }

    public void updateOrganization(Organization organization) {
        this.organization = organization;
    }

    public void increaseParticipants() throws IllegalStateException {
        if (appliedParticipants.get() == this.maxParticipants) {
            throw new IllegalStateException();
        }

        appliedParticipants.incrementAndGet();
    }

    public void decreaseParticipants() throws IllegalStateException {
        if (appliedParticipants.get() <= 0) {
            throw new IllegalStateException();
        }

        appliedParticipants.decrementAndGet();
    }

    public void attendUser(UserVolunteerWork userVolunteerWork) {
        users.add(userVolunteerWork);
    }

    public void leaveUser(UserVolunteerWork userVolunteerWork) {
        users.remove(userVolunteerWork);
    }

    @Override
    public String toString() {
        return "VolunteerWork{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", serviceStartDatetime=" + serviceStartDatetime +
                ", serviceEndDatetime=" + serviceEndDatetime +
                ", serviceDays=" + serviceDays +
                ", targetAudiences=" + targetAudiences +
                ", volunteerTypes=" + volunteerTypes +
                ", recruitStartDateTime=" + recruitStartDateTime +
                ", recruitEndDateTime=" + recruitEndDateTime +
                ", appliedParticipants=" + appliedParticipants +
                ", maxParticipants=" + maxParticipants +
                ", coordinate=" + coordinate +
                ", organization=" + organization +
                "} " + super.toString();
    }
}
