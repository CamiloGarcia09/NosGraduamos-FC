package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.secondaryports.repository.token.FindTokenRepository;
import co.edu.uco.application.secondaryports.repository.token.TokenRepository;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.infraestructure.secondaryadapters.repository.data.TokenSurrealMapper;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.TokenSurrealRepositoryAdapter;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.TokenSurrealModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenSurrealAdapterTest {

    @Mock
    private TokenSurrealRepositoryAdapter tokenSurrealRepositoryAdapter;
    @Mock
    private CatalogPort catalogPort;

    private final TokenSurrealMapper mapper = new TokenSurrealMapper();

    private TokenSurrealAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TokenSurrealAdapter(tokenSurrealRepositoryAdapter, mapper, catalogPort);
    }

    private TokenData sampleData() {
        return new TokenData("id-1", LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 1, 10, 0), "env-1", "secret", "st-1");
    }

    @Test
    void save_upsertsMappedModelAndReturnsData() {
        TokenData data = sampleData();

        TokenData result = adapter.save(data);

        assertThat(result).isSameAs(data);
        verify(tokenSurrealRepositoryAdapter).upsert(org.mockito.ArgumentMatchers.any(TokenSurrealModel.class));
    }

    @Test
    void findById_returnsMappedData_whenFound() {
        TokenSurrealModel model = new TokenSurrealModel("id-1", "secret",
                LocalDateTime.of(2025, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 0), "env-1", "st-1");
        when(tokenSurrealRepositoryAdapter.findTokenSurrealModelById("id-1")).thenReturn(Optional.of(model));

        TokenData result = adapter.findById("id-1");

        assertThat(result.getId()).isEqualTo("id-1");
        assertThat(result.getEnvironmentId()).isEqualTo("env-1");
    }

    @Test
    void findById_throwsBusinessException_whenNotFound() {
        when(tokenSurrealRepositoryAdapter.findTokenSurrealModelById("id-1")).thenReturn(Optional.empty());
        when(catalogPort.getMessage("FUN_049")).thenReturn("Token not found: ");

        assertThatThrownBy(() -> adapter.findById("id-1"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getUserMessage()).isEqualTo("Token not found: id-1"));
    }

    @Test
    void findByEnvironmentAndState_returnsMappedOptional() {
        TokenSurrealModel model = new TokenSurrealModel("id-1", "secret",
                LocalDateTime.of(2025, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 0), "env-1", "st-1");
        when(tokenSurrealRepositoryAdapter.findTokenSurrealModelByEnvironmentIdAndStateId("env-1", "st-1"))
                .thenReturn(Optional.of(model));

        Optional<TokenData> result = adapter.findByEnvironmentAndState("env-1", "st-1");

        assertThat(result).isPresent();
        assertThat(result.get().getEnvironmentId()).isEqualTo("env-1");
    }

    @Test
    void findByEnvironmentAndState_returnsEmpty_whenNone() {
        when(tokenSurrealRepositoryAdapter.findTokenSurrealModelByEnvironmentIdAndStateId("env-1", "st-1"))
                .thenReturn(Optional.empty());

        assertThat(adapter.findByEnvironmentAndState("env-1", "st-1")).isEmpty();
    }
}