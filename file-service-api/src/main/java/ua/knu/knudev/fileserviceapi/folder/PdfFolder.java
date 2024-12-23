package ua.knu.knudev.fileserviceapi.folder;

import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;

import java.util.List;

public enum PdfFolder implements FileFolder<PdfSubfolder> {
    INSTANCE;

    @Override
    public String getName() {
        return "pdfs";
    }

    @Override
    public List<PdfSubfolder> getSubfolders() {
        return List.of(PdfSubfolder.values());
    }
}
