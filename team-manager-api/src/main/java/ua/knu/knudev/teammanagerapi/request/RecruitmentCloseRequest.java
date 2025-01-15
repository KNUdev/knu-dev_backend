package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ua.knu.knudev.teammanagerapi.constant.RecruitmentCloseCause;

import java.util.UUID;

//@Schema(description = "Request model for closing an active recruitment.")
public record RecruitmentCloseRequest(

//        @NotNull
//        @Schema(
//                description = "Unique identifier of the active recruitment that should be closed.",
//                example = "1dce9f3a-3cce-42ce-814c-bcfb3f6df565"
//        )
        UUID activeRecruitmentId,

//        @NotNull
//        @Schema(
//                description = "Reason (cause) for closing the recruitment.",
//                example = "MANUAL_CLOSE"
//        )
        RecruitmentCloseCause closeCause
) {
}
