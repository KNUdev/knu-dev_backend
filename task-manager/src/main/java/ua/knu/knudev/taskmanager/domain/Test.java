package ua.knu.knudev.taskmanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(schema = "task_management", name = "test")
public class Test {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String enName;

    @Column(nullable = false)
    private LocalDate createdAt = LocalDate.now();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestQuestion> testQuestions = new HashSet<>();
}
