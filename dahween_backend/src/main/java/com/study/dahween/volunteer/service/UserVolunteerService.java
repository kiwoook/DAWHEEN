package com.study.dahween.volunteer.service;

import com.study.dahween.organization.repository.OrganRepository;
import com.study.dahween.user.entity.User;
import com.study.dahween.user.repository.UserRepository;
import com.study.dahween.volunteer.entity.UserVolunteerWork;
import com.study.dahween.volunteer.entity.VolunteerWork;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import com.study.dahween.volunteer.repository.UserVolunteerRepository;
import com.study.dahween.volunteer.repository.VolunteerWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVolunteerService {
    private final UserVolunteerRepository userVolunteerRepository;

    @Transactional
    public void deleteUserVolunteerWork(Long userVolunteerWorkId) throws EmptyResultDataAccessException, EntityNotFoundException {
        UserVolunteerWork userVolunteerWork = userVolunteerRepository.findById(userVolunteerWorkId).orElseThrow(EntityNotFoundException::new);

        if (userVolunteerWork.getStatus() == ApplyStatus.APPROVED) {
            User user = userVolunteerWork.getUser();
            VolunteerWork volunteerWork = userVolunteerWork.getVolunteerWork();

            user.leaveVolunteerWork(userVolunteerWork);
            volunteerWork.leaveUser(userVolunteerWork);
        }

        userVolunteerRepository.delete(userVolunteerWork);
    }
}
