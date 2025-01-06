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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.FilterOptions;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/accounts")
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
            @Parameter(name = "searchQuery", description = "Search term to filter accounts by name with email.", example = "John Doe"),
            @Parameter(name = "registrationDate", description = "Start of the registration date range to filter accounts.", example = "2024-01-01T00:00:00", schema = @Schema(type = "string", format = "date-time")),
            @Parameter(name = "registrationEndDate", description = "End of the registration date range to filter accounts.", example = "2024-12-31T23:59:59", schema = @Schema(type = "string", format = "date-time")),
            @Parameter(name = "knudevUnit", description = "Filter accounts by their KNUdev unit.", example = "DEVELOPMENT", schema = @Schema(implementation = KNUdevUnit.class)),
            @Parameter(name = "expertise", description = "Filter accounts by their area of expertise.", example = "SOFTWARE_ENGINEERING", schema = @Schema(implementation = Expertise.class)),
            @Parameter(name = "departmentName", description = "Filter accounts by department name.", example = "Computer Science"),
            @Parameter(name = "specialtyName", description = "Filter accounts by specialty name.", example = "Artificial Intelligence"),
            @Parameter(name = "universityStudyYear", description = "Filter accounts by university study year.", example = "3"),
            @Parameter(name = "recruitmentNumber", description = "Filter accounts by recruitment batch number.", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"),
            @Parameter(name = "technicalRole", description = "Filter accounts by their technical role.", example = "SOFTWARE_ENGINEER", schema = @Schema(implementation = AccountTechnicalRole.class)),
            @Parameter(name = "page", description = "Page number for pagination (0-indexed).", example = "0"),
            @Parameter(name = "size", description = "Number of accounts to retrieve per page.", example = "10")
    })
    @GetMapping
    public Page<AccountProfileDto> getAccountsByFilter(
            @RequestParam(name = "searchQuery", required = false) String searchQuery,
            @RequestParam(name = "registrationDate", required = false) LocalDateTime registrationDate,
            @RequestParam(name = "registrationEndDate", required = false) LocalDateTime registrationEndDate,
            @RequestParam(name = "knudevUnit", required = false) KNUdevUnit knudevUnit,
            @RequestParam(name = "expertise", required = false) Expertise expertise,
            @RequestParam(name = "departmentName", required = false) String departmentName,
            @RequestParam(name = "specialtyName", required = false) String specialtyName,
            @RequestParam(name = "universityStudyYear", required = false) Integer universityStudyYear,
            @RequestParam(name = "recruitmentNumber", required = false) Integer recruitmentNumber,
            @RequestParam(name = "technicalRole", required = false) AccountTechnicalRole technicalRole,
            @RequestParam(name = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "size", defaultValue = "9") Integer pageSize) {

        Map<FilterOptions, Object> filters = createFilters(
                searchQuery, registrationDate, registrationEndDate, knudevUnit, expertise,
                departmentName, specialtyName, universityStudyYear, recruitmentNumber, technicalRole
        );

        return accountProfileApi.findAllBySearchQuery(filters, pageNumber, pageSize);
    }


    private Map<FilterOptions, Object> createFilters(
            String searchQuery, LocalDateTime registrationDate, LocalDateTime registrationEndDate,
            KNUdevUnit knudevUnit, Expertise expertise, String departmentName, String specialtyName,
            Integer universityStudyYear, Integer recruitmentNumber, AccountTechnicalRole technicalRole) {

        Map<FilterOptions, Object> filters = new EnumMap<>(FilterOptions.class);
        addFilter(filters, FilterOptions.USER_INITIALS_AND_EMAIL, searchQuery);
        addFilter(filters, FilterOptions.REGISTRATION_DATE, registrationDate);
        addFilter(filters, FilterOptions.REGISTRATION_DATE_END_TIME, registrationEndDate);
        addFilter(filters, FilterOptions.KNUDEV_UNIT, knudevUnit);
        addFilter(filters, FilterOptions.EXPERTISE, expertise);
        addFilter(filters, FilterOptions.DEPARTMENT, departmentName);
        addFilter(filters, FilterOptions.SPECIALTY, specialtyName);
        addFilter(filters, FilterOptions.UNIVERSITY_STUDY_YEAR, universityStudyYear);
        addFilter(filters, FilterOptions.RECRUITMENT_NUMBER, recruitmentNumber);
        addFilter(filters, FilterOptions.TECHNICAL_ROLE, technicalRole);

        return filters;
    }

    private void addFilter(Map<FilterOptions, Object> filters, FilterOptions option, Object value) {
        if (value != null) {
            filters.put(option, value);
        }
    }
}
