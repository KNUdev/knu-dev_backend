package ua.knu.knudev.teammanagerapi.api;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.knudevsecurityapi.request.AccountCreationRequest;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.AccountSearchCriteria;
import ua.knu.knudev.teammanagerapi.dto.ShortAccountProfileDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.response.AccountRegistrationResponse;
import ua.knu.knudev.teammanagerapi.response.GetAccountByIdResponse;

import java.util.UUID;

public interface AccountProfileApi {
    AccountRegistrationResponse register(@Valid AccountCreationRequest registrationRequest);

    AccountProfileDto getById(UUID id);

    GetAccountByIdResponse getById(String accountProfileId);

    AccountProfileDto getByEmail(String email);

    boolean existsByEmail(String email);

    void assertEmailExists(String email) throws AccountException;

    Page<AccountProfileDto> findAllBySearchQuery(AccountSearchCriteria accountSearchCriteria, Integer page, Integer pageSize);

    Page<ShortAccountProfileDto> findAllTeamMembers(Integer page, Integer pageSize);

    String updateAvatar(UUID accountProfileId, MultipartFile newAvatar);

    void removeAvatar(UUID accountProfileId);

    String updateBanner(UUID accountProfileId, MultipartFile newBanner);

    void removeBanner(UUID accountProfileId);
}
