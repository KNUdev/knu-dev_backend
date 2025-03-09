package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO for detailed information about a subproject's account")
public class SubprojectAccountDto {

    @Schema(description = "Composite key containing identifiers for the subproject and account")
    private SubprojectAccountIdDto subprojectAccountIdDto;

    @Schema(description = "Profile information of the account associated with the subproject")
    private AccountProfileDto accountProfileDto;

    @Schema(description = "Date when the account joined the subproject", example = "2023-06-15")
    private LocalDate dateJoined;

    @Schema(description = "Date when the account left the subproject (null if still active)", example = "2024-02-01")
    private LocalDate dateLeft;

    @Schema(description = "Date of the last commit made by the account in the subproject", example = "2024-02-20")
    private LocalDate lastCommitDate;

    @Schema(description = "Total number of commits made by the account in the subproject", example = "150")
    private Integer totalCommits;

    @Schema(description = "Total number of lines of code written by the account in the subproject", example = "10234")
    private Integer totalLinesOfCodeWritten;
}
