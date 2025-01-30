package ua.knu.knudev.educationapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//todo tests
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EducationProgramDto {
    private UUID id;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private Expertise expertise;
    private boolean isPublished;
    private int version;
    private String finalTaskUrl;
    private List<ProgramSectionDto> sections = new ArrayList<>();

}
