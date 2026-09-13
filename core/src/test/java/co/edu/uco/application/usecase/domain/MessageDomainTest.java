package co.edu.uco.application.usecase.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageDomainTest {

    @Test
    void setters_storeValues() {
        MessageDomain domain = new MessageDomain();
        UUID id = UUID.randomUUID();
        domain.setId(id);
        domain.setCode("CODE");
        domain.setTitle("Title");
        domain.setContent("Content");
        domain.setApplication("app");

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getCode()).isEqualTo("CODE");
        assertThat(domain.getTitle()).isEqualTo("Title");
        assertThat(domain.getContent()).isEqualTo("Content");
        assertThat(domain.getApplication()).isEqualTo("app");
    }

    @Test
    void setters_trimTextFields() {
        MessageDomain domain = new MessageDomain();
        domain.setCode("  CODE  ");
        domain.setTitle("  Title  ");
        domain.setContent("  Content  ");
        domain.setApplication("  app  ");

        assertThat(domain.getCode()).isEqualTo("CODE");
        assertThat(domain.getTitle()).isEqualTo("Title");
        assertThat(domain.getContent()).isEqualTo("Content");
        assertThat(domain.getApplication()).isEqualTo("app");
    }

    @Test
    void setId_usesDefaultWhenNull() {
        MessageDomain domain = new MessageDomain();
        domain.setId(null);

        assertThat(domain.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void setType_storesType() {
        MessageDomain domain = new MessageDomain();
        MessageTypeDomain type = MessageTypeDomain.create(UUID.randomUUID(), "info");
        domain.setType(type);

        assertThat(domain.getType()).isSameAs(type);
    }

    @Test
    void setCategory_storesCategory() {
        MessageDomain domain = new MessageDomain();
        MessageCategoryDomain category = MessageCategoryDomain.create(UUID.randomUUID(), "general");
        domain.setCategory(category);

        assertThat(domain.getCategory()).isSameAs(category);
    }

    @Test
    void setStatus_storesStatus() {
        MessageDomain domain = new MessageDomain();
        MessageStatusDomain status = MessageStatusDomain.create(UUID.randomUUID(), "active");
        domain.setStatus(status);

        assertThat(domain.getStatus()).isSameAs(status);
    }

    @Test
    void setFunctionality_storesFunctionality() {
        MessageDomain domain = new MessageDomain();
        FunctionalityDomain functionality = FunctionalityDomain.create(UUID.randomUUID(), "payment",
                LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.of(2024, 12, 31, 23, 59));
        domain.setFunctionality(functionality);

        assertThat(domain.getFunctionality()).isSameAs(functionality);
    }

}