package ua.knu.knudev.assessmentmanagerapi.api;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

interface BaseTaskApi<T> {
    T upload(MultipartFile file);
    T getById(UUID id);
}
