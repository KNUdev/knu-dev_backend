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
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.teammanagerapi.api.RolePromotionApi;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditionDto;
import ua.knu.knudev.teammanagerapi.request.RolePromotionConditionCreationRequest;
import ua.knu.knudev.teammanagerapi.request.RolePromotionConditionUpdateRequest;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role-promotion")
public class AdminRolePromotionController {

    private final RolePromotionApi rolePromotionApi;

    @Operation(
            summary = "Create a new role promotion condition",
            description = "Creates a new condition that defines when users can be promoted to a new role"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RolePromotionConditionDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/conditions/create")
    public RolePromotionConditionDto create(@RequestBody @Parameter(
            name = "rolePromotionConditionCreationRequest",
            description = "Role promotion condition creation request",
            required = true,
            in = ParameterIn.HEADER,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RolePromotionConditionCreationRequest.class)
            )
    ) @Valid RolePromotionConditionCreationRequest rolePromotionConditionCreationRequest) {
        return rolePromotionApi.createRolePromotionConditions(rolePromotionConditionCreationRequest);
    }

    @Operation(
            summary = "Update an existing role promotion condition",
            description = "Updates an existing condition that defines when users can be promoted to a new role"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RolePromotionConditionDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/conditions/update")
    public RolePromotionConditionDto update(@RequestBody @Parameter(
            name = "rolePromotionConditionUpdateRequest",
            description = "Role promotion condition update request",
            required = true,
            in = ParameterIn.HEADER,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RolePromotionConditionUpdateRequest.class)
            )
    ) @Valid RolePromotionConditionUpdateRequest rolePromotionConditionUpdateRequest) {
        return rolePromotionApi.updateRolePromotionConditions(rolePromotionConditionUpdateRequest);
    }

    @Operation(
            summary = "Delete a role promotion condition",
            description = "Deletes a condition that defines when users can be promoted to a new role"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/conditions/{conditionsId}/delete")
    public void delete(@PathVariable @Parameter(
            name = "conditionsId",
            description = "Role promotion conditions id",
            in = ParameterIn.QUERY,
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000") UUID conditionsId) {
        rolePromotionApi.deleteRolePromotionConditions(conditionsId);
    }

    @Operation(
            summary = "Get role promotion conditions",
            description = "Retrieves the current role promotion conditions"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RolePromotionConditionDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Exception.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Exception.class)
                    ))
    })
    @GetMapping("/conditions")
    public RolePromotionConditionDto getRolePromotionConditions() {
        return rolePromotionApi.getRolePromotionConditions();
    }
}