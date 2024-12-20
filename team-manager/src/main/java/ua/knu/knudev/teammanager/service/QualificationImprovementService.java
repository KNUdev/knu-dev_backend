package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.RecruitmentStatus;
import ua.knu.knudev.knudevcommon.constant.RecruitmentType;
import ua.knu.knudev.teammanager.domain.Recruitment;
import ua.knu.knudev.teammanager.repository.RecruitmentRepository;
import ua.knu.knudev.teammanagerapi.api.QualificationImprovementApi;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentCloseRequestDto;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentCreationRequestDto;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QualificationImprovementService implements QualificationImprovementApi {

    private final RecruitmentRepository recruitmentRepository;

    @Override
    public void initializeRecruitment(RecruitmentCreationRequestDto recruitmentCreationRequestDto) {
        String recruitmentName = recruitmentCreationRequestDto.getRecruitmentName();
        RecruitmentType recruitmentType = recruitmentCreationRequestDto.getRecruitmentType();

        validateRecruitmentData(recruitmentName, recruitmentType);

        Recruitment recruitment = Recruitment.builder()
                .recruitmentName(recruitmentName)
                .recruitmentType(recruitmentType)
                .recruitmentStatus(RecruitmentStatus.ACTIVE_RECRUITMENT)
                .build();

        recruitmentRepository.save(recruitment);
    }

    @Override
    public void closeRecruitment(RecruitmentCloseRequestDto closeRequestDto) {
        Integer recruitmentNumber = closeRequestDto.getRecruitmentNumber();

        if (recruitmentNumber == null) {
            throw new IllegalArgumentException("Recruitment number cannot be null!");
        }

        Recruitment recruitment = recruitmentRepository.findById(recruitmentNumber)
                .orElseThrow(() -> new IllegalArgumentException("Recruitment with number " + recruitmentNumber + " not found!"));

        recruitment.setRecruitmentStatus(RecruitmentStatus.DISABLED_RECRUITMENT);
        recruitment.setRecruitedPeopleNumber(closeRequestDto.getRecruitedPeopleNumber());
        recruitment.setRecruitmentEndDateTime(LocalDateTime.now());

        recruitmentRepository.save(recruitment);
    }

    private void validateRecruitmentData(String recruitmentName, RecruitmentType recruitmentType) {
        if (StringUtils.isBlank(recruitmentName)) {
            throw new IllegalArgumentException("Recruitment name cannot be empty or null!");
        }
        if (recruitmentType == null) {
            throw new IllegalArgumentException("Recruitment type cannot be null!");
        }
    }
}
