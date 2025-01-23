package ua.knu.knudev.fileserviceapi.api;

import ua.knu.knudev.fileserviceapi.subfolder.FileSubfolder;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolderI;

public interface PDFServiceApi<T extends PdfSubfolderI> extends BaseFileServiceApi<T> {
}
