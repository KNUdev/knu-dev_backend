package ua.knu.knudev.taskmanager.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "task_management", name = "task")
public class Task {
    @Id
    @UuidGenerator
    private UUID id;
    private String filename;
    private LocalDateTime additionDate;
    private LocalDateTime lastUpdateDate;
    @Enumerated(EnumType.STRING)
    private AccountTechnicalRole targetTechnicalRole;
}
