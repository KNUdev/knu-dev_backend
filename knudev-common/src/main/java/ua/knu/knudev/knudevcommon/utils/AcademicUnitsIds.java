package ua.knu.knudev.knudevcommon.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Schema(description = "Class representing user's department ID and specialty code name.")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class AcademicUnitsIds {

        @Schema(description = "The user's department ID, which uniquely identifies the department within the organization.",
                example = "d8f1c78a-b7d8-4b9f-975f-0104d6deab82", requiredMode = Schema.RequiredMode.REQUIRED)
        private UUID departmentId;

        @Schema(description = "The specialty code name associated with the user's academic specialization.",
                example = "123.1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Double specialtyCodename;
}

