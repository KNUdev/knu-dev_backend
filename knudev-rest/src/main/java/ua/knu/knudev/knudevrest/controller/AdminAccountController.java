package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.FilterOptions;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
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

    @GetMapping
    public Page<AccountProfileDto> getAccounts(
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
