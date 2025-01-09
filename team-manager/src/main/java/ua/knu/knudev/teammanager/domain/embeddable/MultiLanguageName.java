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
public class MultiLanguageName {
    @Column(nullable = false, unique = true, updatable = false)
    private String enName;

    @Column(nullable = false, unique = true, updatable = false)
    private String ukName;
}
