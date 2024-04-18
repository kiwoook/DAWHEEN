package com.study.dawheen.volunteer.repository;

import com.study.dawheen.user.entity.User;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VolunteerRankingRepositoryTest {

    @Autowired
    UserVolunteerRepository userVolunteerRepository;

    User user1 = User.builder()
            .name("user1")
            .build();

    User user2 = User.builder()
            .name("user2")
            .build();

    User user3 = User.builder()
            .name("user3")
            .build();

    User user4 = User.builder()
            .name("user4")
            .build();

    User user5 = User.builder()
            .name("user5")
            .build();

    User[] users = {user1, user2, user3, user4, user5};

    VolunteerWork volunteerWork = VolunteerWork.builder()
            .build();

    @BeforeEach
    void setup(){
        // 한 5개정도 만들자...
        
        for (User user : users){
            for (int i=0;i<100;i++){
                UserVolunteerWork mockUserVolunteerWork = new UserVolunteerWork(user1, volunteerWork);
                mockUserVolunteerWork.updateStatus(ApplyStatus.COMPLETED);
            }
        }

    }

    //

    @Test
    @DisplayName("월간 랭킹 테스트")
    void monthlyRankingTest(){

    }

}
