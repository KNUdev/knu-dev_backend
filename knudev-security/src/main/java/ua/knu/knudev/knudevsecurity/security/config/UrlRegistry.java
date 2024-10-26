package ua.knu.knudev.knudevsecurity.security.config;

import java.util.Set;

public class UrlRegistry {

    public static final String[] WHITE_LIST_URLS = {
            "/account/register",
            "/auth/login",
            "/test/guest",

            "/error/**",
    };
    public static final Set<String> AUTH_EXCLUDED_URLS = Set.of(
            "/account/create-password"
    );
    public static String AUTH_URL = "/auth";

}
