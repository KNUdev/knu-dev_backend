package ua.knu.knudev.teammanager.domain.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record MultiLanguageName(
        @Column(nullable = false, unique = true, updatable = false)
        String enName,

        @Column(nullable = false, unique = true, updatable = false)
        String ukName
) {
}
