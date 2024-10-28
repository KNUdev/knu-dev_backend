package ua.knu.knudev.knudevsecurity.util;

public class TestsUtils {
    public static String buildJWT(String accessToken) {
        return "Bearer " + accessToken;
    }
}
