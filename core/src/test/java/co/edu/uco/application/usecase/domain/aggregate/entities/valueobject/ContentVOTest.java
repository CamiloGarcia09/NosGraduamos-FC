package co.edu.uco.application.usecase.domain.aggregate.entities.valueobject;

import co.edu.uco.application.crosscutting.exceptions.ContentCanNotBeEmptyException;
import co.edu.uco.application.crosscutting.exceptions.SizeContentLessThanTenException;
import co.edu.uco.application.crosscutting.exceptions.SizeContentMoreThanOneHundred;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContentVOTest {

    @Test
    void constructor_acceptsValidContent() {
        ContentVO vo = new ContentVO("Contenido valido");

        assertThat(vo.getContent()).isEqualTo("Contenido valido");
    }

    @Test
    void constructor_acceptsTenCharacterContent() {
        ContentVO vo = new ContentVO("1234567890");

        assertThat(vo.getContent()).hasSize(10);
    }

    @Test
    void constructor_acceptsOneHundredCharacterContent() {
        String content = "a".repeat(100);

        ContentVO vo = new ContentVO(content);

        assertThat(vo.getContent()).hasSize(100);
    }

    @Test
    void constructor_rejectsEmptyContent() {
        assertThatThrownBy(() -> new ContentVO(""))
                .isInstanceOf(ContentCanNotBeEmptyException.class);
    }

    @Test
    void constructor_rejectsNullContent() {
        assertThatThrownBy(() -> new ContentVO(null))
                .isInstanceOf(ContentCanNotBeEmptyException.class);
    }

    @Test
    void constructor_rejectsContentShorterThanTen() {
        assertThatThrownBy(() -> new ContentVO("corto"))
                .isInstanceOf(SizeContentLessThanTenException.class);
    }

    @Test
    void constructor_rejectsContentLongerThanOneHundred() {
        assertThatThrownBy(() -> new ContentVO("a".repeat(101)))
                .isInstanceOf(SizeContentMoreThanOneHundred.class);
    }

    @Test
    void setContent_updatesValueWhenValid() {
        ContentVO vo = new ContentVO("Contenido valido");
        vo.setContent("Otro contenido");

        assertThat(vo.getContent()).isEqualTo("Otro contenido");
    }
}