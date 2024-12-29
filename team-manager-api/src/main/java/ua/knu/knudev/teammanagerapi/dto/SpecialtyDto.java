package ua.knu.knudev.teammanagerapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageNameDto;

import java.util.Set;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SpecialtyDto(
        Double codeName,
        MultiLanguageNameDto name,
        Set<DepartmentWithSpecialtiesDto> departments) {
}
