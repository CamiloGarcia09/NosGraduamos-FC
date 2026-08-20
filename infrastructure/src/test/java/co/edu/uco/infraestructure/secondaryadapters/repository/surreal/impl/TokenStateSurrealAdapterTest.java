package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.StatusTokenData;
import co.edu.uco.infraestructure.secondaryadapters.repository.data.TokenStateSurrealMapper;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.TokenStateSurrealRepositoryAdapter;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.StatusTokenSurrealModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenStateSurrealAdapterTest {

    @Mock
    private TokenStateSurrealRepositoryAdapter tokenStateSurrealRepositoryAdapter;

    private final TokenStateSurrealMapper mapper = new TokenStateSurrealMapper();

    private TokenStateSurrealAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TokenStateSurrealAdapter(tokenStateSurrealRepositoryAdapter, mapper);
    }

    @Test
    void findByStatus_returnsMappedData() {
        UUID id = UUID.randomUUID();
        StatusTokenSurrealModel model = new StatusTokenSurrealModel(id, "ACTIVE");
        when(tokenStateSurrealRepositoryAdapter.findStatusTokenSurrealModelById("st-1")).thenReturn(model);

        StatusTokenData result = adapter.findByStatus("st-1");

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("ACTIVE");
    }

    @Test
    void findByStatusName_returnsMappedData() {
        StatusTokenSurrealModel model = new StatusTokenSurrealModel(UUID.randomUUID(), "INACTIVE");
        when(tokenStateSurrealRepositoryAdapter.findStatusTokenSurrealModelByName("INACTIVE")).thenReturn(model);

        StatusTokenData result = adapter.findByStatusName("INACTIVE");

        assertThat(result.getName()).isEqualTo("INACTIVE");
    }
}