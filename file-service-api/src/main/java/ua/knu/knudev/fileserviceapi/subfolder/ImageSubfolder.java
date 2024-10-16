package ua.knu.knudev.fileserviceapi.subfolder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ImageSubfolder implements FileSubfolder {
    ACCOUNT_PICTURES("/account/pictures"),
    TASK_PICTURES("/tasks/pictures");

    private final String subfolderPath;

    @Override
    public String getSubfolderPath() {
        return subfolderPath;
    }
}
