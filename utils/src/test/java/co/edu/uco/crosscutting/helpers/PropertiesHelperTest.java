package co.edu.uco.crosscutting.helpers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertiesHelperTest {

    private static final String PROPERTIES_FILE = "test-catalog.properties";

    @Test
    void getValue_returnsValue_whenKeyExists() {
        String value = PropertiesHelper.getValue(PROPERTIES_FILE, "catalog.param.test.value");
        assertThat(value).isEqualTo("test-value");
    }

    @Test
    void getValue_throwsIllegalArgumentException_whenFileNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> PropertiesHelper.getValue(null, "key"));
    }

    @Test
    void getValue_throwsIllegalArgumentException_whenFileNameIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> PropertiesHelper.getValue("  ", "key"));
    }

    @Test
    void getValue_throwsIllegalArgumentException_whenKeyIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> PropertiesHelper.getValue(PROPERTIES_FILE, null));
    }

    @Test
    void getValue_throwsIllegalArgumentException_whenKeyIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> PropertiesHelper.getValue(PROPERTIES_FILE, "  "));
    }

    @Test
    void getValue_throwsRuntimeException_whenFileDoesNotExist() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> PropertiesHelper.getValue("nonexistent-file.properties", "key"));
        assertThat(exception.getMessage()).contains("not found");
    }

    @Test
    void getValue_throwsRuntimeException_whenKeyDoesNotExist() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> PropertiesHelper.getValue(PROPERTIES_FILE, "catalog.param.does.not.exist"));
        assertThat(exception.getMessage()).contains("not found");
    }

    @Test
    void getValue_throwsRuntimeException_whenValueIsBlank() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> PropertiesHelper.getValue(PROPERTIES_FILE, "catalog.param.blank"));
        assertThat(exception.getMessage()).contains("not found");
    }
}