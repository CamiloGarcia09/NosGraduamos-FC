package co.edu.uco.application.usecase;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.application.usecase.handling.HandlingFindEnvironmentIdTokenPort;
import co.edu.uco.crosscutting.exceptions.NotFoundException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilText.getDefault;

@Component
public final class FindEnvironmentIdTokenUseCase implements HandlingFindEnvironmentIdTokenPort {

    public static final String TOKEN_NOT_FOUND = "TOKEN_NOT_FOUND";

    private final FindTokenRepository findTokenRepository;
    private final CatalogPort catalogPort;

    public FindEnvironmentIdTokenUseCase(FindTokenRepository findTokenRepository, CatalogPort catalogPort) {
        this.findTokenRepository = findTokenRepository;
        this.catalogPort = catalogPort;
    }

    @Override
    public String execute(String token) {
        try{
            var tokenData = findTokenRepository.findById(token);
            return getDefault(tokenData.getEnvironmentId());
        }catch (Exception exception){
            var notFound = NotFoundException.buildUserException(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_041.getCode()));
            notFound.setCode(TOKEN_NOT_FOUND);
            throw notFound;
        }
    }
}