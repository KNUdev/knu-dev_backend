package ua.knu.knudev.education.domain.program;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.education.domain.MultiLanguageField;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "module", schema = "education")
@SuperBuilder
public class ProgramModule extends BaseLearningUnit{

}

