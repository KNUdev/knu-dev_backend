package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.RecruitmentApi;
import ua.knu.knudev.teammanagerapi.constant.RecruitmentCloseCause;
import ua.knu.knudev.teammanagerapi.dto.ActiveRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.ClosedRecruitmentDto;
import ua.knu.knudev.teammanagerapi.request.RecruitmentCloseRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentOpenRequest;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/recruitment")
public class AdminRecruitmentController {

    private final RecruitmentApi recruitmentApi;

    @Operation(
            summary = "Open new recruitment",
            description = "This endpoint allows admin to open a new recruitment with providing necessary data."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Recruitment successful opened.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "You do not have access to this endpoint.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping("/open")
    public ActiveRecruitmentDto open(
            @RequestBody @Valid @Parameter(
                    name = "Open request",
                    description = "Recruitment open data",
                    in = ParameterIn.HEADER,
                    required = true,
                    schema = @Schema(implementation = RecruitmentOpenRequest.class)
            ) RecruitmentOpenRequest openRequest) {
        return recruitmentApi.openRecruitment(openRequest);
    }

    @Operation(
            summary = "Close active recruitment",
            description = "This endpoint allows admin to close active recruitment by active recruitment id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recruitment successful closed.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "You do not have access to this endpoint.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @PostMapping("/close")
    public ClosedRecruitmentDto close(
            @RequestBody @Parameter(
                    name = "Active recruitment id",
                    description = "Active recruitment id",
                    in = ParameterIn.HEADER,
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    schema = @Schema(implementation = RecruitmentCloseRequest.class)
            ) UUID activeRecruitmentId) {
        RecruitmentCloseRequest closeRequest = new RecruitmentCloseRequest(
                activeRecruitmentId, RecruitmentCloseCause.MANUAL_CLOSE);
        return recruitmentApi.closeRecruitment(closeRequest);
    }

}
