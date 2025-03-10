package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.knudev.educationapi.api.SessionApi;
import ua.knu.knudev.educationapi.request.SessionCreationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/session")
public class AdminEducationSessionController {
    private final SessionApi sessionApi;

    @PostMapping()
    public void create(@RequestBody SessionCreationRequest sessionCreationRequest) {
        sessionApi.createSession(sessionCreationRequest);
    }
}
