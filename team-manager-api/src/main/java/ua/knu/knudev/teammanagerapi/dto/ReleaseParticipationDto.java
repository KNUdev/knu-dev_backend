package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO for detailed information about a release participation")
public class ReleaseParticipationDto {

    @Schema(description = "ID of the release participation")
    private UUID id;

    @Schema(description = "Release participation`s account profile")
    private AccountProfileDto accountProfile;

    @Schema(description = "Release participation`s subproject account role snapshot")
    private AccountTechnicalRole roleSnapshot;

    @Schema(description = "Release participation`s commit count", example = "42")
    private Integer commitCount;
}
