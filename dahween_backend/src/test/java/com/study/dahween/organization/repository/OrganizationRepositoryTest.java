package com.study.dahween.organization.repository;

import com.study.dahween.common.dto.CoordinateDto;
import com.study.dahween.common.entity.Address;
import com.study.dahween.organization.entity.Organization;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrganizationRepositoryTest {

    @Autowired
    OrganRepository organRepository;

    @BeforeEach
    void setup() {
        Organization organization1 = Organization.builder()
                .name("Organization1")
                .facilityPhone("123-456-7890")
                .email("org1@example.com")
                .facilityType("Type1")
                .representName("Rep1")
                .address(new Address("Street1", "City1", "Zip1"))
                .build();

        organization1.updateCoordinate(new CoordinateDto(100.0, 100.0));

        // 2번 인스턴스
        Organization organization2 = Organization.builder()
                .name("Organization2")
                .facilityPhone("987-654-3210")
                .email("org2@example.com")
                .facilityType("Type2")
                .representName("Rep2")
                .address(new Address("Street2", "City2", "Zip2"))
                .build();

        organization2.updateCoordinate(new CoordinateDto(100.0, 100.00003));

        // 3번 인스턴스
        Organization organization3 = Organization.builder()
                .name("Organization3")
                .facilityPhone("111-222-3333")
                .email("org3@example.com")
                .facilityType("Type3")
                .representName("Rep3")
                .address(new Address("Street3", "City3", "Zip3"))
                .build();

        organization3.updateCoordinate(new CoordinateDto(100.0, 160.0));

        organRepository.save(organization1);
        organRepository.save(organization2);
        organRepository.save(organization3);
    }

    @Test
    void findOrganization() {

        // when
        List<Organization> organizationsWithinRadius = organRepository.findOrganizationsWithinRadius(100.0, 100.0, 50).orElseThrow(EntityNotFoundException::new);
        // then
        assertThat(organizationsWithinRadius).hasSize(2);
    }
}
