package co.edu.uco.application.common.mapper.entity.impl;

import co.edu.uco.application.primaryports.dto.token.TokenDTO;
import co.edu.uco.application.secondaryports.entity.TokenData;
import co.edu.uco.application.usecase.domain.TokenDomain;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;
import static org.assertj.core.api.Assertions.assertThat;

class TokenEntityMapperTest {

    private final TokenEntityMapper mapper = new TokenEntityMapper(new ModelMapper());

    @Test
    void mapperDomain_mapsEntityToDomain() {
        TokenData data = new TokenData();
        data.setId("id-1");
        data.setSecretName("secret");
        data.setCreationDate(LocalDateTime.now());
        data.setExpirationDate(LocalDateTime.now().plusDays(1));
        data.setEnvironmentId(UUID.randomUUID().toString());
        data.setStateId(UUID.randomUUID().toString());

        TokenDomain domain = mapper.mapperDomain(data);

        assertThat(domain.getId()).isEqualTo("id-1");
        assertThat(domain.getSecretName()).isEqualTo("secret");
    }

    @Test
    void mapperData_mapsDomainToEntity() {
        UUID envId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        TokenDomain domain = new TokenDomain("id-1", now, now.plusDays(1), envId, "secret", UUID.randomUUID());

        TokenData data = mapper.mapperData(domain);

        assertThat(data.getId()).isEqualTo("id-1");
        assertThat(data.getSecretName()).isEqualTo("secret");
    }

    @Test
    void mapperDTO_mapsEmptyEntityToDTOWithDefaults() {
        TokenData data = new TokenData();

        TokenDTO dto = mapper.mapperDTO(data);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEmpty();
        assertThat(dto.getSecretName()).isEmpty();
        assertThat(dto.getEnvironmentId()).isEqualTo(DEFAULT_UUID);
    }
}