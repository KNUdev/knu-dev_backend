package ua.knu.knudev.knudevrest.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.RecruitmentApi;
import ua.knu.knudev.teammanagerapi.dto.FullClosedRecruitmentDto;
import ua.knu.knudev.teammanagerapi.request.RecruitmentJoinRequest;
import ua.knu.knudev.teammanagerapi.request.RecruitmentReceivingRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment")
public class RecruitmentController {

    private final RecruitmentApi recruitmentApi;

    @Operation(
            summary = "Join to active recruitment",
            description = "This endpoint allows user to join to active recruitment by recruitment join request data"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully joined to active recruitment",
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
    @PostMapping("/join")
    public void joinActiveRecruitment(
            @RequestBody @Parameter(
                    name = "Recruitment join request",
                    description = "Recruitment join request data",
                    schema = @Schema(implementation = RecruitmentJoinRequest.class),
                    required = true,
                    in = ParameterIn.HEADER
            ) RecruitmentJoinRequest joinRequest) {
        recruitmentApi.joinActiveRecruitment(joinRequest);
    }

    @GetMapping("/closed")
    public FullClosedRecruitmentDto getAllClosedRecruitment(
            @RequestParam RecruitmentReceivingRequest recruitmentReceivingRequest
    ) {
        return;
    }
    //todo close, open recruitment. On close manually to service pass MANUAL_CLOSE


}
