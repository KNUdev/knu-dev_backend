package ua.knu.knudev.teammanager.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.mapper.SpecialtyMapper;
import ua.knu.knudev.teammanager.repository.SpecialtyRepository;
import ua.knu.knudev.teammanagerapi.exception.SpecialtyException;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyMapper specialtyMapper;
    private final SpecialtyRepository specialtyRepository;

    public Specialty getByCodeName(Double codeName) {
        return specialtyRepository.findById(codeName).orElseThrow(() ->
                new SpecialtyException("Specialty with code name " + codeName + " not found!"));
    }
}
