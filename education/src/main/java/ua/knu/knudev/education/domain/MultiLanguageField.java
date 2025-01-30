package ua.knu.knudev.education.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class MultiLanguageField {
    @Column(nullable = false, unique = true, updatable = false)
    private String en;

    @Column(nullable = false, unique = true, updatable = false)
    private String uk;
}

