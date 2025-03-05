package ua.knu.knudev.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.education.domain.program.ProgramTopic;
import ua.knu.knudev.education.mapper.ModuleMapper;
import ua.knu.knudev.education.mapper.ProgramMapper;
import ua.knu.knudev.education.mapper.SectionMapper;
import ua.knu.knudev.education.mapper.TopicMapper;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.dto.ProgramModuleDto;
import ua.knu.knudev.educationapi.dto.ProgramSectionDto;
import ua.knu.knudev.educationapi.dto.ProgramTopicDto;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;

@Component
@RequiredArgsConstructor
public class ProgramMapperDtoHelper {

    private final ProgramMapper programMapper;
    private final SectionMapper sectionMapper;
    private final ModuleMapper moduleMapper;
    private final TopicMapper topicMapper;
    private final PDFServiceApi pdfServiceApi;

    public EducationProgramDto toProgramDto(EducationProgram program) {
        EducationProgramDto programDto = programMapper.toDto(program);
        String programTaskUrl = pdfServiceApi.getPathByFilename(
                program.getFinalTaskFilename(),
                PdfSubfolder.EDUCATION_PROGRAM_PROGRAM_TASKS
        );
        programDto.setFinalTaskUrl(programTaskUrl);
        return programDto;
    }

    public ProgramSectionDto toSectionDto(ProgramSection section) {
        ProgramSectionDto sectionDto = sectionMapper.toDto(section);
        String sectionTaskUrl = pdfServiceApi.getPathByFilename(
                section.getFinalTaskFilename(),
                PdfSubfolder.EDUCATION_PROGRAM_SECTION_TASKS
        );
        sectionDto.setFinalTaskUrl(sectionTaskUrl);
        return sectionDto;
    }

    public ProgramSectionDto toSectionDto(ProgramSection section, ProgramSectionMapping psm) {
        ProgramSectionDto sectionDto = toSectionDto(section);
        sectionDto.setOrderIndex(psm.getOrderIndex());
        return sectionDto;
    }


    public ProgramModuleDto toModuleDto(ProgramModule module) {
        ProgramModuleDto moduleDto = moduleMapper.toDto(module);
        String moduleTaskUrl = pdfServiceApi.getPathByFilename(
                module.getFinalTaskFilename(),
                PdfSubfolder.EDUCATION_PROGRAM_MODULE_TASKS
        );

        moduleDto.setFinalTaskUrl(moduleTaskUrl);
        return moduleDto;
    }

    public ProgramModuleDto toModuleDto(ProgramModule module, SectionModuleMapping smm) {
        ProgramModuleDto moduleDto = toModuleDto(module);
        moduleDto.setOrderIndex(smm.getOrderIndex());
        return moduleDto;
    }

    public ProgramTopicDto toTopicDto(ProgramTopic topic) {
        ProgramTopicDto topicDto = topicMapper.toDto(topic);
        String topicTaskUrl = pdfServiceApi.getPathByFilename(
                topic.getFinalTaskFilename(),
                PdfSubfolder.EDUCATION_PROGRAM_TOPIC_TASKS
        );
        topicDto.setFinalTaskUrl(topicTaskUrl);
        return topicDto;
    }

    public ProgramTopicDto toTopicDto(ProgramTopic topic, ModuleTopicMapping mtm) {
        ProgramTopicDto topicDto = toTopicDto(topic);
        topicDto.setOrderIndex(mtm.getOrderIndex());
        return topicDto;
    }

}
