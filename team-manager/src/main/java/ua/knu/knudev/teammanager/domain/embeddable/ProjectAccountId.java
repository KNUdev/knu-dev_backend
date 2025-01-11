package ua.knu.knudev.teammanager.domain.embeddable;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Embeddable
public class ProjectAccountId implements Serializable {
    private UUID projectId;
    private UUID accountId;
}
