package ua.knu.knudev.knudevrest.config;

import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
public class MentorUpdateRequestDto {
    private Set<UUID> mentorIds;
}
