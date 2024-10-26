package ua.knu.knudev.knudevcommon.mapper;

import java.util.List;
import java.util.Set;

public interface BaseMapper<Domain, Dto> {

    Domain toDomain(Dto dto);

    Dto toDto(Domain domain);

    List<Domain> toDomains(List<Dto> dtos);

    List<Dto> toDtos(List<Domain> domains);

    Set<Dto> toDtos(Set<Domain> domains);

}