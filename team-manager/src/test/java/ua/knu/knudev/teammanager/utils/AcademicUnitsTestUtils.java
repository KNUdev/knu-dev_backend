package ua.knu.knudev.teammanager.utils;

import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;

import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_DEPARTMENT_ID;
import static ua.knu.knudev.teammanager.utils.constants.DepartmentTestsConstants.TEST_SPECIALTY_ID;

public class AcademicUnitsTestUtils {

    public static Specialty getTestSpecialty(String nameInUkrainian, String nameInEnglish) {
        return new Specialty(TEST_SPECIALTY_ID, nameInEnglish, nameInUkrainian);
    }

    public static Department getTestDepartment() {
        Department department = new Department();
        department.setId(TEST_DEPARTMENT_ID);
        department.setName(new MultiLanguageField("Engineering", "Інженерія"));

        department.addSpecialty(getTestSpecialty("Кібербезпека", "Cybersecurity"));
        department.addSpecialty(getTestSpecialty("Комп'ютерні науки", "Computer science"));
        department.addSpecialty(getTestSpecialty("Комп'ютерна електроніка", "Computer electronics"));

        return department;
    }

}
