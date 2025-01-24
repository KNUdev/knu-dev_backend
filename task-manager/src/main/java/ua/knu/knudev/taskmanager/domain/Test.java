package ua.knu.knudev.taskmanager.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "task_management", name = "test")
public class Test {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private LocalDate createdAt;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestQuestion> testQuestions = new HashSet<>();
}
