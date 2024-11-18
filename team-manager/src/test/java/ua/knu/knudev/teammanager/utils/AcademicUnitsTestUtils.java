package ua.knu.knudev.teammanager.utils;

import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;

import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_DEPARTMENT_ID;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_SPECIALTY_ID;

public class AcademicUnitsTestUtils {

    public static Specialty getTestSpecialty(String nameInUkrainian, String nameInEnglish) {
        Specialty specialty = new Specialty();
        specialty.setCodeName(TEST_SPECIALTY_ID);
        specialty.setNameInEnglish(nameInEnglish);
        specialty.setNameInUkrainian(nameInUkrainian);

        return specialty;
    }

    public static Department getTestDepartment() {
        Department department = new Department();
        department.setId(TEST_DEPARTMENT_ID);
        department.setNameInEnglish("Engineering");
        department.setNameInUkrainian("Інженерія");

        department.addSpecialty(getTestSpecialty("Кібербезпека", "Cybersecurity"));
        department.addSpecialty(getTestSpecialty("Комп'ютерні науки", "Computer science"));
        department.addSpecialty(getTestSpecialty("Комп'ютерна електроніка", "Computer electronics"));

        return department;
    }

}
