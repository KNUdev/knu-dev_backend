package ua.knu.knudev.assessmentmanager.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.assessmentmanagerapi.dto.Task;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "assessment_management", name = "task")
public class TaskDomain implements Task {
    @Id
    @UuidGenerator
    private UUID id;
    private String filename;
    private LocalDateTime additionDate;
    private LocalDateTime lastUpdateDate;
    //todo perhaps remove
    @Enumerated(EnumType.STRING)
    private AccountTechnicalRole targetTechnicalRole;
}
