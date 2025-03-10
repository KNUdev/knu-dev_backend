package ua.knu.knudev.educationapi.api;

import ua.knu.knudev.educationapi.dto.session.SessionFullDto;
import ua.knu.knudev.educationapi.request.SessionCreationRequest;

import java.util.List;
import java.util.UUID;

public interface SessionApi {
    //    SessionDto create(SessionCreationRequest sessionCreationRequest);
//    List<SprintDto> generateSprintsForProgram(UUID programId);
    SessionFullDto createSession(SessionCreationRequest request);
}