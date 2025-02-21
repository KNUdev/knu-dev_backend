package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.SubprojectType;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO for detailed information about a subproject")
public class SubprojectDto {

    @Schema(description = "Unique identifier of the subproject", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Type of the subproject", example = "BACKEND")
    private SubprojectType type;

    @Schema(description = "Resource URL of the subproject", example = "https://github.com/KNUdev/knu-dev_backend")
    private String resourceUrl;

    @Schema(description = "Set of releases associated with the subproject")
    private Set<ReleaseDto> releases;

    @Schema(description = "Set of all developers involved in the subproject")
    private Set<SubprojectAccountDto> allDevelopers;
}
