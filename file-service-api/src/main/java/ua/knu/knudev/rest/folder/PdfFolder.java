package ua.knu.knudev.rest.folder;

import ua.knu.knudev.rest.subfolder.PdfSubfolder;

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
