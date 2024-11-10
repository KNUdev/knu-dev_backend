package ua.knu.knudev.taskmanager.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(schema = "security_management", name = "task_assignment")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskAssignment {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @OneToOne(optional = false)
    @JoinColumn(name = "task_id", unique = true)
    private Task task;

    @Column(nullable = false, unique = true)
    private String verificationCode;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime activationExpiryDate;

    @Column(nullable = false)
    private LocalDateTime dispatchExpiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskAssignmentStatus status;

}
