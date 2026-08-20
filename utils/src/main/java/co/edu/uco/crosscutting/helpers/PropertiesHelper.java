package co.edu.uco.crosscutting.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;

public final class PropertiesHelper {

    private PropertiesHelper() {
    }

    public static String getValue(final String propertiesFileName, final String key) {
        if (isEmptyOrNull(propertiesFileName)) {
            throw new IllegalArgumentException("Properties file name cannot be null or empty");
        }

        if (isEmptyOrNull(key)) {
            throw new IllegalArgumentException("Key cannot be null or empty when reading from: " + propertiesFileName);
        }

        try (InputStream inputStream = PropertiesHelper.class.getClassLoader()
                .getResourceAsStream(propertiesFileName)) {

            if (inputStream == null) {
                throw new RuntimeException("Properties file not found: " + propertiesFileName);
            }

            var properties = new Properties();
            properties.load(inputStream);
            var value = properties.getProperty(key);

            if (isEmptyOrNull(value)) {
                throw new RuntimeException("Key '" + key + "' not found in: " + propertiesFileName);
            }

            return value;

        } catch (final RuntimeException exception) {
            throw exception;
        } catch (final IOException exception) {
            throw new RuntimeException("Error reading properties file: " + propertiesFileName, exception);
        }
    }
}
