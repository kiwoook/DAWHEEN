package com.study.dawheen.volunteer.service;

import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVolunteerService {

    private final UserVolunteerRepository userVolunteerRepository;
    private final UserRepository userRepository;

    @Transactional
    public void unregisterUsersFromVolunteerWork(Long volunteerWorkId) {
        List<UserVolunteerWork> userVolunteerWorks = userVolunteerRepository
                .findAllByVolunteerWorkIdWithFetch(volunteerWorkId)
                .orElseThrow(() -> new EntityNotFoundException("VolunteerWork with ID :" + volunteerWorkId + " not found"));

        for (UserVolunteerWork userVolunteerWork : userVolunteerWorks) {
            User user = userVolunteerWork.getUser();
            user.leaveVolunteerWork(userVolunteerWork);
        }

        userRepository.saveAll(userVolunteerWorks.stream().map(UserVolunteerWork::getUser).toList());
    }

    @Transactional
    public void deleteApprovedUserVolunteerWork(Long userVolunteerWorkId) throws EmptyResultDataAccessException, EntityNotFoundException {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findById(userVolunteerWorkId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() == ApplyStatus.APPROVED) {
            User user = userVolunteerWork.getUser();
            VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();

            user.leaveVolunteerWork(userVolunteerWork);
            volunteerWork.leaveUser(userVolunteerWork);
        }

        userVolunteerRepository.delete(userVolunteerWork);
    }


    public Page<VolunteerInfoResponseDto> getParticipateVolunteerWorkByUser(String email, Pageable pageable) {
        return userVolunteerRepository.findVolunteerWorkByEmailAndStatus(email, ApplyStatus.COMPLETED, pageable);

    }
}
