package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ua.knu.knudev.teammanagerapi.dto.SubprojectAccountDto;

import java.util.Set;
import java.util.UUID;

@Builder
@Schema(description = "Request DTO for updating subproject details")
public record SubprojectUpdateRequest(

        @Schema(description = "Unique identifier of the subproject", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank(message = "Subproject's id cannot be blank")
        @NotNull(message = "Subproject's id cannot be null")
        UUID id,

        @Schema(description = "Set of accounts associated with the subproject", implementation = SubprojectAccountDto.class)
        @NotEmpty(message = "Subproject accounts list can not be empty")
        Set<SubprojectAccountDto> subprojectAccountDtos
) {
}
