package ua.knu.knudev.assessmentmanager.domain;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseTask {
    @Id
    @UuidGenerator
    private UUID id;
    private String taskFilename;
    @CreationTimestamp
    private LocalDateTime additionDate = LocalDateTime.now();
    private LocalDateTime lastUpdateDate;
}
