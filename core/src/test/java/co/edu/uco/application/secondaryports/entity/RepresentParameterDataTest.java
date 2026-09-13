package co.edu.uco.application.secondaryports.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RepresentParameterDataTest {

    @Test
    void defaultConstructor_setsDefaults() {
        RepresentParameterData data = new RepresentParameterData();

        assertThat(data.getId()).isNotNull();
        assertThat(data.getStart()).isEmpty();
        assertThat(data.getEnd()).isEmpty();
        assertThat(data.getApplication()).isNotNull();
        assertThat(data.isDefaultParameter()).isTrue();
        assertThat(data.isParameter()).isTrue();
    }

    @Test
    void parameterizedConstructor_storesValues() {
        UUID id = UUID.randomUUID();
        ApplicationData application = ApplicationData.build();
        RepresentParameterData data = new RepresentParameterData(id, " start ", " end ", application, false, false);

        assertThat(data.getId()).isEqualTo(id);
        assertThat(data.getStart()).isEqualTo("start");
        assertThat(data.getEnd()).isEqualTo("end");
        assertThat(data.getApplication()).isSameAs(application);
        assertThat(data.isDefaultParameter()).isFalse();
        assertThat(data.isParameter()).isFalse();
    }

    @Test
    void setters_trimAndApplyDefaults() {
        RepresentParameterData data = new RepresentParameterData();

        data.setId(null);
        data.setStart("  a  ");
        data.setEnd(null);
        data.setApplication(null);
        data.setDefaultParameter(false);
        data.setParameter(false);

        assertThat(data.getId())
                .isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertThat(data.getStart()).isEqualTo("a");
        assertThat(data.getEnd()).isEmpty();
        assertThat(data.getApplication()).isNotNull();
        assertThat(data.isDefaultParameter()).isFalse();
        assertThat(data.isParameter()).isFalse();
    }
}