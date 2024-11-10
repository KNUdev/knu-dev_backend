package ua.knu.knudev.taskmanager.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(schema = "security_management", name = "verification_code")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskVerificationCode {
    @Id
    @UuidGenerator
    private UUID id;

    private LocalDateTime activationExpiryDate;
    private LocalDateTime dispatchExpiryDate;
    private String verificationCode;

    @OneToOne
    private Task task;

}
