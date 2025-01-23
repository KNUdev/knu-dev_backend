package ua.knu.knudev.education.domain.program;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.MultiLanguageField;
import ua.knu.knudev.education.domain.TaskProxy;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "module", schema = "education")
public class ProgramModule {

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

    @OneToOne(optional = false)
    @JoinColumn(name = "task_id", unique = true)
    private TaskProxy moduleFinalTask;

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
}

