package com.study.dawheen.common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COORDINATE_ID", nullable = false)
    private Long id;

    private Double latitude;

    private Double longitude;


    public Coordinate(Double latitude, Double longitude) {
        if (latitude == null && longitude == null) {
            throw new IllegalArgumentException("Both latitude and longitude cannot be null.");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
