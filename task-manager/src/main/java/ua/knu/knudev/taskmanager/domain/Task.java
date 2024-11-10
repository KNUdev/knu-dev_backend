package ua.knu.knudev.taskmanager.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "task_management", name = "task")
public class Task {

    @Id
    private UUID id;
    private String filename;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "verification_code_id", referencedColumnName = "id")
    private TaskVerificationCode taskVerificationCode;
}
