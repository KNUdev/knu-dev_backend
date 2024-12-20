package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.RecruitmentStatus;
import ua.knu.knudev.teammanager.domain.Recruitment;
import ua.knu.knudev.teammanager.repository.RecruitmentRepository;
import ua.knu.knudev.teammanagerapi.api.QualificationImprovementApi;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentCloseRequestDto;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentOpenRequestDto;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QualificationImprovementService implements QualificationImprovementApi {

    private final RecruitmentRepository recruitmentRepository;

    @Override
    public void initializeRecruitment(RecruitmentOpenRequestDto recruitmentOpenRequestDto) {
        String recruitmentName = recruitmentOpenRequestDto.getRecruitmentName();
        Expertise expertise = recruitmentOpenRequestDto.getExpertise();

        validateRecruitmentData(recruitmentName, expertise);

        Recruitment recruitment = Recruitment.builder()
                .recruitmentName(recruitmentName)
                .expertise(expertise)
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

    private void validateRecruitmentData(String recruitmentName, Expertise expertise) {
        if (StringUtils.isBlank(recruitmentName)) {
            throw new IllegalArgumentException("Recruitment name cannot be empty or null!");
        }
        if (expertise == null) {
            throw new IllegalArgumentException("Recruitment type cannot be null!");
        }
    }
}
