package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.dto.AccountSearchCriteria;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.request.AccountUpdateRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/accounts")
@Validated
public class AdminAccountController {

    private final AccountProfileApi accountProfileApi;

    @Operation(
            summary = "Get accounts by filter",
            description = """
                        This endpoint allows an admin to retrieve a paginated list of authors based on various filters.
                        All filters are optional, and if none are provided, the endpoint will return all accounts.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of authors.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountProfileDto.class)
                    )),
            @ApiResponse(
                    responseCode = "403",
                    description = "You do not have access to this endpoint.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
    })
    @Parameters({
            @Parameter(name = "searchQuery", description = "Search term to filter accounts by name or email.", example = "John Doe"),
            @Parameter(name = "registeredAt", description = "Exact date of registration.", example = "2024-01-01T00:00:00", schema = @Schema(type = "string", format = "date-time")),
            @Parameter(name = "registeredBefore", description = "End of the registration date range to filter accounts.", example = "2024-12-31T23:59:59", schema = @Schema(type = "string", format = "date-time")),
            @Parameter(name = "unit", description = "Filter accounts by their unit.", example = "CAMPUS", schema = @Schema(implementation = KNUdevUnit.class)),
            @Parameter(name = "expertise", description = "Filter accounts by their area of expertise.", example = "FULLSTACK", schema = @Schema(implementation = Expertise.class)),
            @Parameter(name = "departmentId", description = "Filter accounts by department id.", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"),
            @Parameter(name = "specialtyCodeName", description = "Filter accounts by specialty code name.", example = "123.0"),
            @Parameter(name = "universityStudyYear", description = "Filter accounts by university study year.", example = "3"),
            @Parameter(name = "recruitmentId", description = "Filter accounts by recruitment id.", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"),
            @Parameter(name = "technicalRole", description = "Filter accounts by their technical role.", example = "INTERN", schema = @Schema(implementation = AccountTechnicalRole.class)),
            @Parameter(name = "pageNumber", description = "Page number for pagination (0-indexed).", example = "0"),
            @Parameter(name = "pageSize", description = "Number of accounts to retrieve per page.", example = "10")
    })
    @GetMapping
    public Page<AccountProfileDto> getAccountsByFilter(
            @RequestParam(name = "searchQuery", required = false) String searchQuery,
            @RequestParam(name = "registeredAt", required = false) LocalDateTime registeredAt,
            @RequestParam(name = "registeredBefore", required = false) LocalDateTime registeredBefore,
            @RequestParam(name = "unit", required = false) KNUdevUnit unit,
            @RequestParam(name = "expertise", required = false) Expertise expertise,
            @RequestParam(name = "departmentId", required = false) String departmentId,
            @RequestParam(name = "specialtyCodeName", required = false) String specialtyCodeName,
            @RequestParam(name = "universityStudyYear", required = false) Integer universityStudyYear,
            @RequestParam(name = "recruitmentId", required = false) UUID recruitmentId,
            @RequestParam(name = "technicalRole", required = false) AccountTechnicalRole technicalRole,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize) {

        AccountSearchCriteria accountSearchCriteria = AccountSearchCriteria.builder()
                .searchQuery(searchQuery)
                .registeredAt(registeredAt)
                .registeredBefore(registeredBefore)
                .unit(unit)
                .expertise(expertise)
                .departmentId(departmentId)
                .specialtyCodeName(specialtyCodeName)
                .universityStudyYear(universityStudyYear)
                .recruitmentId(recruitmentId)
                .technicalRole(technicalRole)
                .build();

        return accountProfileApi.findAllBySearchQuery(accountSearchCriteria, pageNumber, pageSize);
    }

    @Operation(
            summary = "Update an account",
            description = "Allows updating user account details, including email, GitHub username, department, and other fields.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Account successfully updated",
                            content = @Content(schema =
                            @Schema(implementation = AccountProfileDto.class))),
                    @ApiResponse(responseCode = "404", description = "Account not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
            })
    @Parameters({
            @Parameter(name = "accountId", description = "UUID of the account to update", required = true),
            @Parameter(name = "firstName", description = "User's first name"),
            @Parameter(name = "lastName", description = "User's last name"),
            @Parameter(name = "middleName", description = "User's middle name"),
            @Parameter(name = "email", description = "User's email address", example = "user@knu.ua"),
            @Parameter(name = "technicalRole", description = "User's technical role"),
            @Parameter(name = "yearsOfStudyOnRegistration", description = "Years of study at the time of registration"),
            @Parameter(name = "unit", description = "User's unit"),
            @Parameter(name = "gitHubAccountUsername", description = "GitHub account username"),
            @Parameter(name = "specialtyCodeName", description = "Specialty code"),
            @Parameter(name = "departmentId", description = "Department ID")
    })
    @PostMapping("/{accountId}/update")
    public AccountProfileDto updateAccount(
            @PathVariable UUID accountId,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "middleName", required = false) String middleName,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "technicalRole", required = false) AccountTechnicalRole technicalRole,
            @RequestParam(name = "yearsOfStudyOnRegistration", required = false) Integer yearsOfStudyOnRegistration,
            @RequestParam(name = "unit", required = false) KNUdevUnit unit,
            @RequestParam(name = "gitHubAccountUsername", required = false) String gitHubAccountUsername,
            @RequestParam(name = "specialtyCodeName", required = false) Double specialtyCodeName,
            @RequestParam(name = "departmentId", required = false) UUID departmentId
    ) {
        AccountUpdateRequest accountUpdateRequest = AccountUpdateRequest.builder()
                .accountId(accountId)
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .email(email)
                .technicalRole(technicalRole)
                .unit(unit)
                .yearOfStudyOnRegistration(yearsOfStudyOnRegistration)
                .gitHubAccountUsername(gitHubAccountUsername)
                .specialtyCodeName(specialtyCodeName)
                .departmentId(departmentId)
                .build();

        return accountProfileApi.update(accountUpdateRequest);
    }
}
