package co.edu.uco.application.usecase.domain;

import co.edu.uco.crosscutting.helpers.UtilDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalityDomainTest {

    @Test
    void create_storesValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        FunctionalityDomain domain = FunctionalityDomain.create(id, " name ", start, end);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("name");
        assertThat(domain.getStartDate()).isEqualTo(start);
        assertThat(domain.getEndDate()).isEqualTo(end);
    }

    @Test
    void setters_applyDefaultsWhenNull() {
        FunctionalityDomain domain = FunctionalityDomain.create(UUID.randomUUID(), "name", null, null);

        assertThat(domain.getStartDate()).isEqualTo(UtilDate.TIME);
        assertThat(domain.getEndDate()).isEqualTo(UtilDate.TIME);
    }

    @Test
    void setId_usesDefaultWhenNull() {
        FunctionalityDomain domain = FunctionalityDomain.create(null, "name", null, null);

        assertThat(domain.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void setName_trimsValue() {
        FunctionalityDomain domain = FunctionalityDomain.create(UUID.randomUUID(), null, null, null);

        assertThat(domain.getName()).isEmpty();
    }
}