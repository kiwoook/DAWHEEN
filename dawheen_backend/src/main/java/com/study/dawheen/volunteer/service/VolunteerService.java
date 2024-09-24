package com.study.dawheen.volunteer.service;

import com.study.dawheen.infra.file.service.FileService;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.service.OrganSubscribeService;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerCreateRequestDto;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.dto.VolunteerUpdateRequestDto;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolunteerService {


    private final VolunteerWorkRepository volunteerWorkRepository;
    private final UserVolunteerRepository userVolunteerRepository;
    private final OrganSubscribeService organSubscribeService;
    private final FileService fileService;
    private final UserRepository userRepository;

    @Transactional
    public VolunteerInfoResponseDto create(String email, VolunteerCreateRequestDto createResponseDto, MultipartFile file, List<MultipartFile> files) throws IOException, IllegalArgumentException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        VolunteerWork volunteerWork = VolunteerWork.toEntity(createResponseDto);

        // 이미지 저장
        if (file != null) {
            fileService.saveImgFileByVolunteerWork(file, volunteerWork);
        }

        if (files != null && !files.isEmpty()) {
            for (MultipartFile multipartFile : files) {
                fileService.saveImgFileByVolunteerWork(multipartFile, volunteerWork);
            }
        }

        Organization organization = user.getOrganization();
        if (organization != null) {
            volunteerWork.updateOrganization(organization);
            organSubscribeService.sendNotify(organization.getId());
        }

        return new VolunteerInfoResponseDto(volunteerWorkRepository.save(volunteerWork));
    }

    // TODO 해당 봉사활동의 관리자만 삭제나 수정할 수 있도록 해야 함

    @Transactional(timeout = 5)
    public void delete(Long volunteerWorkId) throws EntityNotFoundException {
        List<UserVolunteerWork> userVolunteerWorks = userVolunteerRepository.findAllByVolunteerWorkIdWithFetch(volunteerWorkId).orElseThrow(EntityNotFoundException::new);

        for (UserVolunteerWork userVolunteerWork : userVolunteerWorks) {
            User user = userVolunteerWork.getUser();
            user.leaveVolunteerWork(userVolunteerWork);
        }

//      volunteerWorkRepository.deleteById(volunteerWorkId) 에서 volunteerWorkId가 없으면 에러를 반환하지 않는다.
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);
        volunteerWorkRepository.delete(volunteerWork);
    }

    @Transactional
    public VolunteerInfoResponseDto update(Long volunteerWorkId, VolunteerUpdateRequestDto updateResponseDto) {
        VolunteerWork volunteerWork = volunteerWorkRepository.findById(volunteerWorkId).orElseThrow(EntityNotFoundException::new);
        volunteerWork.update(updateResponseDto.getTitle(), updateResponseDto.getContent(), updateResponseDto.getServiceStartDatetime(), updateResponseDto.getServiceEndDatetime(), updateResponseDto.getServiceDays(), updateResponseDto.getTargetAudiences(), updateResponseDto.getVolunteerTypes(), updateResponseDto.getRecruitStartDateTime(), updateResponseDto.getRecruitEndDateTime(), updateResponseDto.getMaxParticipants(), updateResponseDto.getCoordinate().getLatitude(), updateResponseDto.getCoordinate().getLongitude());

        return new VolunteerInfoResponseDto(volunteerWork);
    }


}

