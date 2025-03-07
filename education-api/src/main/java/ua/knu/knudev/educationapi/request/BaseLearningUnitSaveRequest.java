package ua.knu.knudev.educationapi.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Valid
public class BaseLearningUnitSaveRequest {
    @Valid
    protected MultiLanguageFieldDto name;
    @Valid
    protected MultiLanguageFieldDto description;
    @Valid
    protected MultipartFile finalTask;
}
