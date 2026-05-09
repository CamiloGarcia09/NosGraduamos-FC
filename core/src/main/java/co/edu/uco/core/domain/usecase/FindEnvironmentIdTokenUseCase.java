package co.edu.uco.core.domain.usecase;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.port.out.repository.token.FindTokenRepository;
import co.edu.uco.core.domain.usecase.handling.HandlingFindEnvironmentIdTokenPort;
import co.edu.uco.utils.exception.CrossWordsException;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilText.getDefault;

@Component
public final class FindEnvironmentIdTokenUseCase implements HandlingFindEnvironmentIdTokenPort {
    private final FindTokenRepository findTokenRepository;
    public FindEnvironmentIdTokenUseCase(FindTokenRepository findTokenRepository) {
        this.findTokenRepository = findTokenRepository;
    }
    @Override
    public String execute(String token) {
        try{
            var tokenData = findTokenRepository.findById(token);
            return getDefault(tokenData.getEnvironmentId());
        }catch (Exception exception){
            throw CrossWordsException.build(DetailMessageEnum.FUN_041.getContent());
        }
    }
}