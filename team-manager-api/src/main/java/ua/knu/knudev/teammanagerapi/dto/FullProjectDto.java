package ua.knu.knudev.teammanagerapi.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullProjectDto extends ShortProjectDto {

    private UUID id;
    private LocalDate startedAt;
    private Set<String> githubRepoLinks;
    private ProjectReleaseInfoDto releaseInfo;
    private Set<ProjectAccountDto> projectAccounts;

}