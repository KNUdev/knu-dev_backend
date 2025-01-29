package ua.knu.knudev.education.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
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

    @CreationTimestamp
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Expertise expertise;

    @Column(name = "version", nullable = false, columnDefinition = "int default 1")
    private Integer version;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    @OneToOne(optional = false)
    @JoinColumn(name = "final_task_id", unique = true)
    private EducationTaskProxy finalTask;

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}
