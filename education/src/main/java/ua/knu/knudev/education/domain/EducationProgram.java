package ua.knu.knudev.education.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
//todo maybe education_program, not just program
@Table(name = "program", schema = "education")
@Builder
public class EducationProgram {

    @Id
    @UuidGenerator
    private UUID id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "en_name")),
            @AttributeOverride(name = "uk", column = @Column(name = "uk_name"))
    })
    private MultiLanguageField name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en", column = @Column(name = "en_description")),
            @AttributeOverride(name = "uk", column = @Column(name = "uk_description"))
    })
    private MultiLanguageField description;

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime lastModifiedDate;

    @Column(nullable = false)
    private Expertise expertise;

    private Integer version = 1;

    @OneToOne(optional = false)
    @JoinColumn(name = "final_task_id", unique = true)
    private EducationTaskProxy finalTask;

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
