package ua.knu.knudev.rest.folder;

import ua.knu.knudev.rest.subfolder.FileSubfolder;

import java.util.List;

public interface FileFolder<S extends FileSubfolder> {
    String getName();
    List<S> getSubfolders();
}
