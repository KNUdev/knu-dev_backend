package ua.knu.knudev.educationapi.api;

import ua.knu.knudev.educationapi.dto.SessionDto;
import ua.knu.knudev.educationapi.dto.SessionFullDto;
import ua.knu.knudev.educationapi.request.CreateSessionRequestDto;
import ua.knu.knudev.educationapi.session.SprintDto;
import ua.knu.knudev.educationapi.request.SessionCreationRequest;

import java.util.List;
import java.util.UUID;

public interface SessionApi {
//    SessionDto create(SessionCreationRequest sessionCreationRequest);
//    List<SprintDto> generateSprintsForProgram(UUID programId);
SessionFullDto createSession(CreateSessionRequestDto request);
}
