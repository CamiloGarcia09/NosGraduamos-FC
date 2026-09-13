package co.edu.uco.application.usecase.domain.aggregate.entities;

import co.edu.uco.crosscutting.helpers.UtilDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalityEntityTest {

    @Test
    void setId_usesDefaultUUIDWhenNull() {
        FunctionalityEntity entity = new FunctionalityEntity();

        entity.setId(null);

        assertThat(entity.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void setId_acceptsValidUUID() {
        FunctionalityEntity entity = new FunctionalityEntity();
        UUID id = UUID.randomUUID();

        entity.setId(id);

        assertThat(entity.getId()).isEqualTo(id);
    }

    @Test
    void setName_trimsValue() {
        FunctionalityEntity entity = new FunctionalityEntity();

        entity.setName("  functionality name  ");

        assertThat(entity.getName()).isEqualTo("functionality name");
    }

    @Test
    void setName_acceptsValidName() {
        FunctionalityEntity entity = new FunctionalityEntity();

        entity.setName("auth-service");

        assertThat(entity.getName()).isEqualTo("auth-service");
    }

    @Test
    void setStartDate_usesDefaultTimeWhenNull() {
        FunctionalityEntity entity = new FunctionalityEntity();

        entity.setStartDate(null);

        assertThat(entity.getStartDate()).isEqualTo(UtilDate.TIME);
    }

    @Test
    void setStartDate_acceptsValidDate() {
        FunctionalityEntity entity = new FunctionalityEntity();
        LocalDateTime date = LocalDateTime.now().plusDays(10);

        entity.setStartDate(date);

        assertThat(entity.getStartDate()).isEqualTo(date);
    }

    @Test
    void setEndDate_usesDefaultTimeWhenNull() {
        FunctionalityEntity entity = new FunctionalityEntity();

        entity.setEndDate(null);

        assertThat(entity.getEndDate()).isEqualTo(UtilDate.TIME);
    }

    @Test
    void setEndDate_acceptsValidDate() {
        FunctionalityEntity entity = new FunctionalityEntity();
        LocalDateTime date = LocalDateTime.now().plusDays(30);

        entity.setEndDate(date);

        assertThat(entity.getEndDate()).isEqualTo(date);
    }
}
