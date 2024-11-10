package ua.knu.knudev.intergrationtests.utils.constants;

import ua.knu.knudev.knudevcommon.utils.FullName;
import ua.knu.knudev.knudevcommon.constant.AccountRole;

public class AccountTestsConstants {

    public static final String TEST_EMAIL = "testKnuDevEmail@knu.ua";
    public static final String TEST_PASSWORD = "qwerty12345";
    public static final String TEST_FILE_NAME = "testFilename";
    public static final AccountRole TEST_ROLE = AccountRole.INTERN;

    public static final String PROFILE_FIRST_NAME = "John";
    public static final String PROFILE_LAST_NAME = "Snow";
    public static final String PROFILE_MIDDLE_NAME = "Bastard";

    public static final FullName TEST_FULLNAME = new FullName(
            PROFILE_FIRST_NAME, PROFILE_LAST_NAME, PROFILE_MIDDLE_NAME
    );

}
