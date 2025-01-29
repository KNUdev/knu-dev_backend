package ua.knu.knudev.assessmentmanagerapi.api;

import java.util.UUID;

interface BaseTaskApi<T> {
    T getById(UUID id);
}
