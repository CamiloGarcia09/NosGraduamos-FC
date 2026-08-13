package co.edu.uco.application.usecase;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.application.usecase.handling.HandlingFindEnvironmentIdTokenPort;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilText.getDefault;

@Component
public final class FindEnvironmentIdTokenUseCase implements HandlingFindEnvironmentIdTokenPort {
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
            throw CrossWordsException.build(catalogPort.getMessage("FUN_041"));
        }
    }
}