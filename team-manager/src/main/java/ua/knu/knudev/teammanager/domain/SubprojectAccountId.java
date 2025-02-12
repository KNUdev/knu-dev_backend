package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Embeddable
public class SubprojectAccountId {
    private UUID subprojectId;
    private UUID accountId;
}
