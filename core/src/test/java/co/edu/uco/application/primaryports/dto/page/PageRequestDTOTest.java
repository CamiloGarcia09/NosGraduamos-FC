package co.edu.uco.application.primaryports.dto.page;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageRequestDTOTest {

    private PageRequestDTO buildPageRequest() {
        return PageRequestDTO.builder()
                .page("1")
                .size("50")
                .columnSort("id")
                .sort("ASC")
                .build();
    }

    @Test
    void builder_createsDtoWithValues() {
        PageRequestDTO dto = buildPageRequest();

        assertThat(dto.getPage()).isEqualTo("1");
        assertThat(dto.getSize()).isEqualTo("50");
        assertThat(dto.getColumnSort()).isEqualTo("id");
        assertThat(dto.getSort()).isEqualTo("ASC");
    }

    @Test
    void setters_updateValues() {
        PageRequestDTO dto = new PageRequestDTO();
        dto.setPage("2");
        dto.setSize("10");
        dto.setColumnSort("code");
        dto.setSort(null);

        assertThat(dto.getPage()).isEqualTo("2");
        assertThat(dto.getSize()).isEqualTo("10");
        assertThat(dto.getColumnSort()).isEqualTo("code");
        assertThat(dto.getSort()).isNull();
    }

    @Test
    void equals_returnsTrueForSameValuesAndSameHashCode() {
        PageRequestDTO a = buildPageRequest();
        PageRequestDTO b = buildPageRequest();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_returnsTrueWhenBothFieldsAreNull() {
        PageRequestDTO a = new PageRequestDTO();
        PageRequestDTO b = new PageRequestDTO();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_returnsFalseForNullAndDifferentType() {
        PageRequestDTO a = buildPageRequest();

        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("1");
    }

    @Test
    void equals_returnsFalseWhenFieldDiffers() {
        PageRequestDTO a = buildPageRequest();
        PageRequestDTO b = buildPageRequest();
        b.setSize("100");

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_returnsFalseWhenOneFieldIsNullAndOtherIsNot() {
        PageRequestDTO a = buildPageRequest();
        PageRequestDTO b = buildPageRequest();
        b.setSort(null);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toString_containsFieldValues() {
        String value = buildPageRequest().toString();

        assertThat(value).contains("1", "50", "id", "ASC");
    }
}