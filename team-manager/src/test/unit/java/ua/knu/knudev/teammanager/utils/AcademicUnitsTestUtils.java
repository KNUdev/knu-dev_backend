package ua.knu.knudev.teammanager.utils;

import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;

import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_DEPARTMENT_ID;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_SPECIALTY_ID;

public class AcademicUnitsTestUtils {

    public static Specialty getTestSpecialty(String name) {
        Specialty specialty = new Specialty();
        specialty.setCodeName(TEST_SPECIALTY_ID);
        specialty.setName(name);

        return specialty;
    }

    public static Department getTestDepartment() {
        Department department = new Department();
        department.setId(TEST_DEPARTMENT_ID);
        department.setName("Engineering");

//        department.addSpecialty(getTestSpecialty("Cybersecurity"));
//        department.addSpecialty(getTestSpecialty("Computer science"));
        department.addSpecialty(getTestSpecialty("Computer electronics"));

        return department;
    }

}
