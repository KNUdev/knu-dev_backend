package ua.knu.knudev.knudevrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevsecurityapi.response.ErrorResponse;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/project")
//todo test what Page<Project> returns. Decide to use Page<?> or List<?>
public class ProjectController {

    private final ProjectApi projectApi;

    @Operation(
            summary = "Retrieve project details by ID",
            description = "Fetches detailed information about a project based on the provided project ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project was successfully retrieved.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FullProjectDto.class)
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public FullProjectDto getProject(@PathVariable @Parameter(
            name = "projectId",
            description = "Id of the project, information about which we want to see",
            in = ParameterIn.HEADER,
            schema = @Schema(implementation = FullProjectDto.class),
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
    ) UUID projectId) {
        return projectApi.getById(projectId);
    }

    @Operation(
            summary = "Retrieve a list of projects",
            description = "Fetches information about all projects in organization."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Projects were successfully retrieved.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ShortProjectDto.class)
            ))
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Page<ShortProjectDto> getAllProjects(
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize) {
        return projectApi.getAll(pageNumber, pageSize);
    }

    @GetMapping("/{accountId}/all")
    public List<ShortProjectDto> getAllByAccountId(@PathVariable UUID accountId) {
        //todo finish when the projects structure will be done
        return List.of(
                ShortProjectDto.builder()
                        .banner("")
//                                .tags()
                        .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
                        .name("Super Project")
                        .status(ProjectStatus.UNDER_DEVELOPMENT)
                        .build(),
                ShortProjectDto.builder()
                        .banner("")
//                                .tags()
                        .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
                        .name("Super Project")

                        .status(ProjectStatus.UNDER_DEVELOPMENT)
                        .build(),
                ShortProjectDto.builder()
                        .banner("")
//                                .tags()
                        .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
                        .name("Super Project")

                        .status(ProjectStatus.UNDER_DEVELOPMENT)
                        .build(),
                ShortProjectDto.builder()
                        .banner("")
//                                .tags()
                        .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
                        .name("Super Project")

                        .status(ProjectStatus.UNDER_DEVELOPMENT)
                        .build(),ShortProjectDto.builder()
                        .banner("")
//                                .tags()
                        .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
                        .name("Super Project")

                        .status(ProjectStatus.UNDER_DEVELOPMENT)
                        .build(),ShortProjectDto.builder()
                        .banner("")
//                                .tags()
                        .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
                        .name("Super Project")

                        .status(ProjectStatus.UNDER_DEVELOPMENT)
                        .build(),ShortProjectDto.builder()
                        .banner("")
//                                .tags()
                        .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
                        .name("Super Project")

                        .status(ProjectStatus.UNDER_DEVELOPMENT)
                        .build(),ShortProjectDto.builder()
                        .banner("")
//                                .tags()
                        .description(new MultiLanguageFieldDto("Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності.", "Уявіть світ, де люди з народження мають особливу \"світлову іскру\", яка дозволяє їм впливати на один із природних елементів (воду, вогонь, землю чи повітря). Але є умова: іскра активується лише після досягнення певного життєвого моменту, який визначається не віком, а силою емоційного потрясіння або справжньою самопожертвою. Головний герой – підліток, який все ще не знайшов свою іскру, хоча всі навколо вже отримали свої здібності."))
                        .name("Super Project")

                        .status(ProjectStatus.UNDER_DEVELOPMENT)
                        .build()
        );

    }


}
