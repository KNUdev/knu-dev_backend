package ua.knu.knudev.teammanager.utils;

import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.knudevcommon.utils.AcademicUnitsIds;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.knudevcommon.utils.FullName;

import static ua.knu.knudev.teammanager.utils.AcademicUnitsTestUtils.getTestDepartment;
import static ua.knu.knudev.teammanager.utils.AcademicUnitsTestUtils.getTestSpecialty;
import static ua.knu.knudev.teammanager.utils.constants.AccountTestsConstants.*;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_DEPARTMENT_ID;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_SPECIALTY_ID;

public class AccountTestUtils {

    public static AccountProfile  getTestAccountProfile() {
        return AccountProfile.builder()
                .id(1)
                .email(TEST_EMAIL)
                .firstName(PROFILE_FIRST_NAME)
                .lastName(PROFILE_LAST_NAME)
                .middleName(PROFILE_MIDDLE_NAME)
                .avatarFilename(TEST_FILE_NAME)
                .department(getTestDepartment())
                .specialty(getTestSpecialty("Computer science"))
                .build();
    }

    public static AccountProfileDto getTestAccountProfileDto() {
        return AccountProfileDto.builder()
                .email(TEST_EMAIL)
                .roles(TEST_ROLES)
                .fullName(new FullName(PROFILE_FIRST_NAME, PROFILE_LAST_NAME, PROFILE_MIDDLE_NAME))
                .academicUnitsIds(AcademicUnitsIds.builder()
                        .departmentId(TEST_DEPARTMENT_ID)
                        .specialtyCodename(TEST_SPECIALTY_ID)
                        .build())
                .avatarFilename(TEST_FILE_NAME)
                .build();
    }

}
