package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.dto.ShortAccountProfileDto;

@RestController
@AllArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final AccountProfileApi accountProfileApi;

    @Operation(
            summary = "Retrieve information about team members",
            description = "Fetches a paginated list of team members with details like name, technical role, and avatar."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Team members were successfully retrieved.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShortAccountProfileDto.class)
                    ))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ShortAccountProfileDto> getOurTeam(
            @RequestParam(name = "pageNumber", defaultValue = "0") @Parameter(
                    name = "pageNumber",
                    description = "The page number to retrieve (zero-based index).",
                    in = ParameterIn.HEADER,
                    example = "0"
            ) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "9") @Parameter(
                    name = "pageSize",
                    description = "The number of team members to retrieve per page.",
                    in = ParameterIn.HEADER,
                    example = "9"
            ) Integer pageSize) {
        return accountProfileApi.findAllTeamMembers(pageNumber, pageSize);
    }
}

