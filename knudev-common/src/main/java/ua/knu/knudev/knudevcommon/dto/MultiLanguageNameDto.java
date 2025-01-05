package ua.knu.knudev.knudevcommon.dto;

import lombok.*;

@Builder
@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MultiLanguageNameDto {
    private String enName;
    private String ukName;
}
