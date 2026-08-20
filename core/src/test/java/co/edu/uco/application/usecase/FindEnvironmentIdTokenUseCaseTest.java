package co.edu.uco.application.usecase;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindEnvironmentIdTokenUseCaseTest {

    @Mock
    private FindTokenRepository findTokenRepository;

    @Mock
    private CatalogPort catalogPort;

    @InjectMocks
    private FindEnvironmentIdTokenUseCase useCase;

    @Test
    void execute_returnsEnvironmentId_whenTokenExists() {
        TokenData token = new TokenData();
        token.setEnvironmentId("env-123");
        when(findTokenRepository.findById("token")).thenReturn(token);

        String result = useCase.execute("token");

        assertThat(result).isEqualTo("env-123");
    }

    @Test
    void execute_returnsEmptyString_whenEnvironmentIdIsNull() {
        TokenData token = new TokenData();
        when(findTokenRepository.findById("token")).thenReturn(token);

        String result = useCase.execute("token");

        assertThat(result).isEmpty();
    }

    @Test
    void execute_throwsCrossWordsException_whenTokenNotFound() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_041.getCode())).thenReturn("Token not found");
        when(findTokenRepository.findById("missing")).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute("missing"))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("Token not found"));
    }
}