package ua.knu.knudev.teammanagerapi.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record SpecialtyDto(
        Double codeName,
        String nameInEnglish,
        String nameInUkrainian,
        Set<DepartmentDto> departments) {
}
