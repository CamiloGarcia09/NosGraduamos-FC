package co.edu.uco.application.usecase.domain.aggregate.entities.valueobject;

import co.edu.uco.application.crosscutting.exceptions.SizeTitleLessThanTenException;
import co.edu.uco.application.crosscutting.exceptions.SizeTitleMoreThanFiftyException;
import co.edu.uco.application.crosscutting.exceptions.TitleCanNotBeEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TitleVOTest {

    @Test
    void constructor_acceptsValidTitle() {
        TitleVO vo = new TitleVO("Titulo valido");

        assertThat(vo.getTitle()).isEqualTo("Titulo valido");
    }

    @Test
    void constructor_acceptsTenCharacterTitle() {
        TitleVO vo = new TitleVO("1234567890");

        assertThat(vo.getTitle()).hasSize(10);
    }

    @Test
    void constructor_acceptsFiftyCharacterTitle() {
        String title = "a".repeat(50);

        TitleVO vo = new TitleVO(title);

        assertThat(vo.getTitle()).hasSize(50);
    }

    @Test
    void constructor_rejectsEmptyTitle() {
        assertThatThrownBy(() -> new TitleVO(""))
                .isInstanceOf(TitleCanNotBeEmptyException.class);
    }

    @Test
    void constructor_rejectsNullTitle() {
        assertThatThrownBy(() -> new TitleVO(null))
                .isInstanceOf(TitleCanNotBeEmptyException.class);
    }

    @Test
    void constructor_rejectsTitleShorterThanTen() {
        assertThatThrownBy(() -> new TitleVO("corta"))
                .isInstanceOf(SizeTitleLessThanTenException.class);
    }

    @Test
    void constructor_rejectsTitleLongerThanFifty() {
        assertThatThrownBy(() -> new TitleVO("a".repeat(51)))
                .isInstanceOf(SizeTitleMoreThanFiftyException.class);
    }

    @Test
    void setTitle_updatesValueWhenValid() {
        TitleVO vo = new TitleVO("Titulo valido");
        vo.setTitle("Otro titulo");

        assertThat(vo.getTitle()).isEqualTo("Otro titulo");
    }
}