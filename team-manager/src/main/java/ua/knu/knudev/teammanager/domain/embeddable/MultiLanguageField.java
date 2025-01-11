package ua.knu.knudev.teammanager.domain.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class MultiLanguageField {
    @Column(nullable = false, unique = true, updatable = false)
    private String en;

    @Column(nullable = false, unique = true, updatable = false)
    private String uk;
}
