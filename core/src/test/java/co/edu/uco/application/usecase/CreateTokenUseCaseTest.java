package co.edu.uco.application.usecase;

import co.edu.uco.application.common.mapper.entity.impl.TokenEntityMapper;
import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.secondaryports.repository.token.TokenRepository;
import co.edu.uco.application.usecase.domain.TokenDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTokenUseCaseTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private TokenEntityMapper tokenEntityMapper;

    @InjectMocks
    private CreateTokenUseCase useCase;

    @Test
    void createToken_setsActiveStateAndPersists() {
        TokenDomain domain = new TokenDomain();
        TokenData data = new TokenData();
        when(tokenEntityMapper.mapperData(domain)).thenReturn(data);
        when(tokenRepository.save(data)).thenReturn(data);

        useCase.createToken(domain);

        verify(tokenEntityMapper).mapperData(domain);
        verify(tokenRepository).save(data);
        verify(tokenEntityMapper).mapperDomain(data);
        org.assertj.core.api.Assertions.assertThat(domain.getStateId().toString())
                .isEqualTo("123e4567-e89b-12d3-a456-426614175000");
    }
}