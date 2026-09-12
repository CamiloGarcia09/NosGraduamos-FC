package co.edu.uco.application.secondaryports.repository;

/**
 * Catálogos de referencia a los que apuntan los enlaces de registro de la base de datos.
 * Cada constante identifica la tabla con la que se vinculan los id de los catálogos
 * (por ejemplo: `application_state:active`, `environment_type:develop`).
 */
public enum ReferenceCatalog {

    APPLICATION_STATE("application_state"),
    ENVIRONMENT_STATE("environment_state"),
    ENVIRONMENT_TYPE("environment_type"),
    FUNCTIONALITY_STATE("functionality_state"),
    LANGUAGE_BASE("language_base");

    private final String table;

    ReferenceCatalog(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }
}