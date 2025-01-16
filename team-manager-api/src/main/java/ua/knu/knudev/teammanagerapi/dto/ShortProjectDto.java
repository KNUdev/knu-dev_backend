package ua.knu.knudev.teammanagerapi.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.ProjectTag;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ShortProjectDto {

    protected MultiLanguageFieldDto name;
    private MultiLanguageFieldDto description;
    private ProjectStatus status;
    private String avatarFilename;
    private Set<ProjectTag> tags;

}
