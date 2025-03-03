package ua.knu.knudev.teammanagerapi.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ua.knu.knudev.educationapi.dto.EducationProgramSummaryDto;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.teammanagerapi.dto.ShortDepartmentDto;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;
import ua.knu.knudev.teammanagerapi.dto.ShortSpecialtyDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GetAccountByIdResponse(
        String fullName,
        String githubAccountUsername,
        String email,
        Expertise expertise,
        ShortDepartmentDto department,
        ShortSpecialtyDto specialty,
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                timezone = "UTC"
        )
        LocalDateTime registeredAt,
        String avatarImageUrl,
        String bannerImageUrl,
        AccountTechnicalRole technicalRole,
        //todo
        //todo list education programs
        List<EducationProgramSummaryDto> completedEducationPrograms,
        List<ShortProjectDto> projects
) {
}
