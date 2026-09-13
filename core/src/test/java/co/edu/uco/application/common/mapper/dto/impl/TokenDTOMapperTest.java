package co.edu.uco.application.common.mapper.dto.impl;

import co.edu.uco.application.primaryports.dto.token.TokenDTO;
import co.edu.uco.application.usecase.domain.TokenDomain;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenDTOMapperTest {

    private final TokenDTOMapper mapper = new TokenDTOMapper(new ModelMapper());

    @Test
    void mapperDomain_mapsDtoToDomain() {
        UUID envId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        TokenDTO dto = TokenDTO.builder()
                .id("id-1")
                .secretName("secret")
                .creationDate(now)
                .expirationDate(now.plusDays(1))
                .environmentId(envId)
                .build();

        TokenDomain domain = mapper.mapperDomain(dto);

        assertThat(domain.getId()).isEqualTo("id-1");
        assertThat(domain.getSecretName()).isEqualTo("secret");
        assertThat(domain.getCreationDate()).isEqualTo(now);
        assertThat(domain.getExpirationDate()).isEqualTo(now.plusDays(1));
        assertThat(domain.getEnvironmentId()).isEqualTo(envId);
    }

    @Test
    void mapperDTO_mapsDomainToDto() {
        UUID envId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        TokenDomain domain = new TokenDomain("id-1", now, now.plusDays(1), envId, "secret", UUID.randomUUID());

        TokenDTO dto = mapper.mapperDTO(domain);

        assertThat(dto.getId()).isEqualTo("id-1");
        assertThat(dto.getSecretName()).isEqualTo("secret");
        assertThat(dto.getCreationDate()).isEqualTo(now);
        assertThat(dto.getExpirationDate()).isEqualTo(now.plusDays(1));
        assertThat(dto.getEnvironmentId()).isEqualTo(envId);
    }
}