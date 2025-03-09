package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO for detailed information about a subproject's account ID")
public class SubprojectAccountIdDto {

    @Schema(description = "Unique identifier of the subproject", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID subprojectId;

    @Schema(description = "Unique identifier of the account", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID accountId;
}
