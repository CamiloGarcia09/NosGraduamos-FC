package co.edu.uco.core.domain.usecase;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.port.out.repository.token.FindTokenRepository;
import co.edu.uco.core.domain.usecase.handling.HandlingFindEnvironmentIdTokenPort;
import co.edu.uco.utils.exception.CrossWordsException;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilText.getDefault;

@Component
public final class FindEnvironmentIdTokenUseCase implements HandlingFindEnvironmentIdTokenPort {
    private final FindTokenRepository findTokenRepository;
    private final InMemoryCatalog inMemoryCatalog;
    public FindEnvironmentIdTokenUseCase(FindTokenRepository findTokenRepository, InMemoryCatalog inMemoryCatalog) {
        this.findTokenRepository = findTokenRepository;
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public String execute(String token) {
        try{
            var tokenData = findTokenRepository.findById(token);
            return getDefault(tokenData.getEnvironmentId());
        }catch (Exception exception){
            throw CrossWordsException.build(inMemoryCatalog.getContent(MessageKeyEnum.FUN_041.getKey()));
        }
    }
}