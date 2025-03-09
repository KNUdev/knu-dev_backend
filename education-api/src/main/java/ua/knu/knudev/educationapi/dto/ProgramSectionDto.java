package ua.knu.knudev.educationapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSectionDto {
    private UUID id;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private String finalTaskUrl;
    private String finalTaskFilename;
    private List<ProgramModuleDto> modules = new ArrayList<>();
    private Integer orderIndex;
}
