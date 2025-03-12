package ua.knu.knudev.education.domain.program;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.MultiLanguageField;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "topic", schema = "education")
@SuperBuilder
@BatchSize(size = 100)
public class ProgramTopic extends BaseLearningUnit{

    @ElementCollection
    @CollectionTable(
            schema = "education",
            name = "topic_learning_resources",
            joinColumns = @JoinColumn(name = "topic_id")
    )
    @Column(name = "learning_resource", nullable = false)
    private Set<String> learningResources = new HashSet<>();

    private UUID testId;

    @Column(nullable = false)
    private Integer difficulty;
}
