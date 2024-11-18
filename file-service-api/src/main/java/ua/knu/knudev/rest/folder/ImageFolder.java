package ua.knu.knudev.rest.folder;

import ua.knu.knudev.rest.subfolder.ImageSubfolder;

import java.util.List;

public enum ImageFolder implements FileFolder<ImageSubfolder> {
    INSTANCE;

    @Override
    public String getName() {
        return "images";
    }

    @Override
    public List<ImageSubfolder> getSubfolders() {
        return List.of(ImageSubfolder.values());
    }
}