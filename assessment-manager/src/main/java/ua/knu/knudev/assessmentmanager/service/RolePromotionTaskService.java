package ua.knu.knudev.assessmentmanager.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanager.domain.RolePromotionTask;
import ua.knu.knudev.assessmentmanager.mapper.RolePromotionTaskMapper;
import ua.knu.knudev.assessmentmanager.repository.RolePromotionTaskRepository;
import ua.knu.knudev.assessmentmanagerapi.api.RolePromotionTaskApi;
import ua.knu.knudev.assessmentmanagerapi.dto.RolePromotionTaskDto;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskException;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolderI;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.exception.FileException;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.exception.AccountException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePromotionTaskService implements RolePromotionTaskApi {

    private final RolePromotionTaskRepository rolePromotionTaskRepository;
    private final PDFServiceApi pdfServiceApi;
    private final AccountProfileApi accountProfileApi;
    private final RolePromotionTaskMapper rolePromotionTaskMapper;

    @Override
    public String uploadTaskForRole(String stringAccountTechnicalRole, MultipartFile file, String accountEmail) {
        validateFilename(file);

        AccountTechnicalRole accountRole = AccountTechnicalRole.getFromString(stringAccountTechnicalRole);
        PdfSubfolderI subfolder = PdfSubfolder.ROLE_ASSIGNMENTS_TASK.forRole(accountRole);
        String savedTaskFilename = saveTask(file.getOriginalFilename(), accountRole, accountEmail);

        return pdfServiceApi.uploadFile(file, savedTaskFilename, subfolder);
    }

    @Override
    public Set<RolePromotionTaskDto> getAllTasksByAccountEmail(String accountEmail) {
        Set<RolePromotionTask> rolePromotionTasks = rolePromotionTaskRepository.getAllByCreatorAccountEmail(accountEmail).orElseThrow(
                () -> new TaskException(
                        String.format("No tasks found for account with email: %s", accountEmail),
                        HttpStatus.NOT_FOUND
                ));
        return rolePromotionTaskMapper.toDtos(rolePromotionTasks);
    }

    private String saveTask(String taskFilename, AccountTechnicalRole targetRole, String accountEmail) {
        assertTaskDoesNotExist(taskFilename);
        if (ObjectUtils.isEmpty(targetRole)) {
            throw new TaskException(
                    "Target role cannot be null, while uploading task: " + taskFilename,
                    HttpStatus.BAD_REQUEST
            );
        }
        if (!accountProfileApi.existsByEmail(accountEmail)) {
            throw new AccountException(
                    String.format("Account with email %s does not exist", accountEmail),
                    HttpStatus.BAD_REQUEST
            );
        }

        RolePromotionTask task = RolePromotionTask.builder()
                .lastUpdateDate(LocalDateTime.now())
                .taskFilename(taskFilename)
                .targetTechnicalRole(targetRole)
                .creatorAccountEmail(accountEmail)
                .build();

        RolePromotionTask savedTask = rolePromotionTaskRepository.save(task);
        return savedTask.getTaskFilename();
    }

    private void validateFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String noExtensionFilename = removeExtension(filename);

        final String ROLE_PATTERN = generateTaskFilenameRolePattern();
        final String CAMEL_CASE_PATTERN = "([A-Z][a-z0-9]+)+";
        final String FILENAME_PATTERN = "^Task_(" + ROLE_PATTERN + ")_(" + CAMEL_CASE_PATTERN + ")$";

        Pattern pattern = Pattern.compile(FILENAME_PATTERN);
        Matcher matcher = pattern.matcher(noExtensionFilename);

        if (!matcher.matches()) {
            throw new FileException(
                    String.format("Invalid filename naming: %s", filename)
            );
        }
    }

    private String generateTaskFilenameRolePattern() {
        return Arrays.stream(AccountTechnicalRole.values())
                .map(Enum::name)
                .collect(Collectors.joining("|"));
    }

    private String removeExtension(String filename) {
        if (StringUtils.isEmpty(filename)) {
            throw new TaskException("Filename is empty", HttpStatus.BAD_REQUEST);
        }

        int dotIndex = filename.indexOf(".");
        return filename.substring(0, dotIndex);
    }

    private void assertTaskDoesNotExist(String filename) {
        boolean taskExists = rolePromotionTaskRepository.existsByTaskFilename(filename);
        if (taskExists) {
            throw new TaskException(
                    String.format("TaskDomain %s already exists", filename), HttpStatus.BAD_REQUEST
            );
        }
    }

    //todo
    @Override
    public String getById(UUID id) {
        return "";
    }
}
