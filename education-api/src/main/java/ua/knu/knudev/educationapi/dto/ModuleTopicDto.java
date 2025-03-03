package ua.knu.knudev.educationapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleTopicDto {
    private UUID id;
    private MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private Set<String> learningResources;
    private String finalTaskUrl;
    private String finalTaskFilename;
    private int difficulty;
    private UUID testId;
    private Integer orderIndex;
}
