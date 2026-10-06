package co.edu.uco.infraestructure.deployment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Contrato estático del script de inicialización de SurrealDB.
 *
 * <p>El comportamiento semántico real (import ejecutable, {@code created_at} inmutable,
 * {@code updated_at} recalculado y reejecución del init) se valida contra una instancia
 * desechable de SurrealDB 3.1.4; no puede ejecutarse dentro de Maven porque la tubería de
 * CI ejecuta {@code mvn clean verify} dentro de un contenedor Java sin acceso al demonio de
 * Docker. Esta clase fija en cada build el contrato declarativo que hace posible esas
 * invariantes: definiciones de campo, captura de la hora de la semilla y recreación
 * idempotente de los registros deterministas.</p>
 */
class SurrealInitScriptContractTest {

    private static final Path SCRIPT_RELATIVE_PATH =
            Path.of("deployment", "docker", "scripts", "surreal", "surreal-init.surql");

    private static final Pattern COMMENT = Pattern.compile("--[^\\r\\n]*");
    private static final Pattern CREATED_AT_DEFINITION =
            Pattern.compile("^DEFINE FIELD\\s+(?:IF NOT EXISTS\\s+|OVERWRITE\\s+)?created_at\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATED_AT_DEFINITION =
            Pattern.compile("^DEFINE FIELD\\s+(?:IF NOT EXISTS\\s+|OVERWRITE\\s+)?updated_at\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern FIELD_TABLE = Pattern.compile("\\bON\\s+([a-z_]+)\\b",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern VALUE_COMPUTATION = Pattern.compile("\\bVALUE\\b");
    private static final Pattern FIXED_DATETIME_LITERAL = Pattern.compile("\\bd'\\d{4}-\\d{2}-\\d{2}T");
    private static final Pattern TIMESTAMP_ASSIGNMENT =
            Pattern.compile("\\b(created_at|updated_at|generated_at)\\s*:\\s*([^,}\\r\\n]+)");
    private static final Pattern RECORD_TARGET = Pattern.compile("^(UPSERT|DELETE)\\s+([a-z_]+:`[^`]+`)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern INIT_NOW_CAPTURE =
            Pattern.compile("^LET\\s+\\$init_now\\s*=\\s*time::now\\(\\)", Pattern.CASE_INSENSITIVE);

    private static final Set<String> TABLES_WITH_IMMUTABLE_CREATED_AT = Set.of(
            "domain_events", "organization", "external_identity", "application", "environment",
            "membership", "role", "role_assignment", "functionality", "message", "message_environment");

    private static final Set<String> TABLES_WITH_MUTABLE_UPDATED_AT = Set.of(
            "organization", "external_identity", "application", "environment", "active_context",
            "functionality", "message", "message_environment");

    private String script;

    @BeforeEach
    void loadInitScript() {
        script = readInitScript();
    }

    @Nested
    @DisplayName("created_at es inmutable en las once tablas que lo declara")
    class ImmutableCreatedAt {

        @Test
        @DisplayName("Toda definición de created_at usa DEFAULT time::now() con READONLY, nunca VALUE")
        void createdAtDefinitions_elevenTables_useDefaultTimeNowAndReadonly() {
            List<String> violations = createdAtFieldViolations(script);

            assertAll(
                    () -> assertThat(violations).isEmpty(),
                    () -> assertThat(tablesDefining(script, CREATED_AT_DEFINITION))
                            .containsExactlyInAnyOrderElementsOf(TABLES_WITH_IMMUTABLE_CREATED_AT));
        }

        @Test
        @DisplayName("Los ocho updated_at declarados siguen usando VALUE time::now() para recalcularse")
        void updatedAtDefinitions_eightTables_keepValueTimeNow() {
            assertThat(tablesDefining(script, UPDATED_AT_DEFINITION))
                    .containsExactlyInAnyOrderElementsOf(TABLES_WITH_MUTABLE_UPDATED_AT);
        }

        @Test
        @DisplayName("Se denuncia created_at cuando alguien lo devuelve a VALUE time::now()")
        void createdAtDefinitions_reportViolation_whenReadOnlyIsReplacedByValue() {
            String regression = script.replace(
                    "ON organization TYPE datetime DEFAULT time::now() READONLY",
                    "ON organization TYPE datetime VALUE time::now()");

            assertThat(createdAtFieldViolations(regression))
                    .anySatisfy(violation -> assertThat(violation).contains("organization"));
        }
    }

    @Nested
    @DisplayName("La semilla toma la hora de la captura $init_now en vez de fechas fijas")
    class SeedTimestampsUseCapturedNow {

        @Test
        @DisplayName("Todo created_at, updated_at o generated_at de la semilla vale $init_now")
        void seedTimestamps_assignInitNow_toEveryTimestampField() {
            assertAll(
                    () -> assertThat(seedTimestampViolations(script)).isEmpty(),
                    () -> assertThat(fixedDatetimeLiterals(script)).isEmpty());
        }

        @Test
        @DisplayName("Un apóstrofe de comentario no se interpreta como fecha fija, pero una fecha real sí")
        void fixedDateDetection_ignoresComments_butDetectsRealLiterals() {
            String commentedOnly = "-- la fecha seed's d'2026-01-01T00:00:00Z queda fuera\n"
                    + "UPSERT organization:`8f2d3a10-6b47-4c91-a5e8-2d7f9b3c1a40` CONTENT "
                    + "{ created_at: $init_now };";
            String withRealLiteral = commentedOnly.replace("$init_now", "d'2026-01-01T00:00:00Z'");

            assertAll(
                    () -> assertThat(fixedDatetimeLiterals(commentedOnly)).isEmpty(),
                    () -> assertThat(fixedDatetimeLiterals(withRealLiteral)).hasSize(1));
        }

        @Test
        @DisplayName("Se denuncia la fecha fija 2026-01-01 si vuelve a la semilla")
        void seedTimestamps_reportViolation_whenFixedDateIsRestored() {
            String regression = script.replace("created_at: $init_now",
                    "created_at: d'2026-01-01T00:00:00Z'");

            assertThat(seedTimestampViolations(regression))
                    .anySatisfy(violation -> assertThat(violation).contains("organization"));
        }

        @Test
        @DisplayName("Se denuncia la semilla si desaparece la captura LET $init_now = time::now()")
        void seedTimestamps_reportViolation_whenInitNowCaptureIsMissing() {
            String regression = script.replace("LET $init_now = time::now();", "");

            assertThat(seedTimestampViolations(regression))
                    .anySatisfy(violation -> assertThat(violation).contains("$init_now"));
        }
    }

    @Nested
    @DisplayName("El init puede reejecutarse porque la semilla se borra antes de recrearse")
    class ReExecutionIsIdempotent {

        @Test
        @DisplayName("Cada registro de la semilla se elimina antes de su UPSERT y no se duplica")
        void seedRecords_areDeletedBeforeUpsert() {
            assertThat(reExecutionViolations(script)).isEmpty();
        }

        @Test
        @DisplayName("La semilla recrea los diecinueve registros deterministas sin repetir ninguno")
        void seedRecords_keepNineteenDeterministicTargets() {
            List<String> targets = seedUpsertTargets(script);

            assertAll(
                    () -> assertThat(targets).doesNotHaveDuplicates(),
                    () -> assertThat(targets).hasSize(19),
                    () -> assertThat(targets).contains("organization:`8f2d3a10-6b47-4c91-a5e8-2d7f9b3c1a40`",
                            "message:`b6d3f821-7a4e-49c5-8d12-3f0b9e6a274c`",
                            "message_environment_readmodel:`2e7a4c91-6b35-48fd-a2e8-1c9d5f703b64`"));
        }

        @Test
        @DisplayName("Se denuncia el UPSERT del mensaje si se elimina su DELETE previo")
        void seedRecords_reportViolation_whenDeleteBeforeUpsertIsRemoved() {
            String regression = script.replace(
                    "DELETE message:`b6d3f821-7a4e-49c5-8d12-3f0b9e6a274c`;", "");

            assertThat(reExecutionViolations(regression))
                    .anySatisfy(violation -> assertThat(violation)
                            .contains("message:`b6d3f821-7a4e-49c5-8d12-3f0b9e6a274c`"));
        }
    }

    private static String readInitScript() {
        Path scriptPath = findInitScript();
        assertThat(scriptPath)
                .as("%s debe existir buscando hacia arriba desde %s", SCRIPT_RELATIVE_PATH,
                        Path.of("").toAbsolutePath())
                .isNotNull();
        try {
            return Files.readString(scriptPath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static Path findInitScript() {
        Path directory = Path.of("").toAbsolutePath();
        while (directory != null) {
            Path candidate = directory.resolve(SCRIPT_RELATIVE_PATH);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
            directory = directory.getParent();
        }
        return null;
    }

    private static List<String> createdAtFieldViolations(String initScript) {
        List<String> violations = new ArrayList<>();
        for (String statement : statements(initScript)) {
            if (!CREATED_AT_DEFINITION.matcher(statement).find()) {
                continue;
            }
            String table = tableOf(statement);
            if (!statement.contains("TYPE datetime")) {
                violations.add("created_at de " + table + " no está declarado como datetime");
            }
            if (!statement.contains("DEFAULT time::now()")) {
                violations.add("created_at de " + table + " debe usar DEFAULT time::now()");
            }
            if (!statement.contains("READONLY")) {
                violations.add("created_at de " + table + " debe declararse READONLY");
            }
            if (VALUE_COMPUTATION.matcher(statement).find()) {
                violations.add("created_at de " + table + " no debe calcularse con VALUE");
            }
        }
        return violations;
    }

    private static List<String> seedTimestampViolations(String initScript) {
        List<String> violations = new ArrayList<>();
        if (initNowCaptureIndex(initScript) < 0) {
            violations.add("Falta la captura LET $init_now = time::now() antes de la semilla");
            return violations;
        }
        List<String> seedUpserts = seedUpserts(initScript);
        if (seedUpserts.isEmpty()) {
            violations.add("La sección posterior a LET $init_now no contiene UPSERT de semilla");
            return violations;
        }
        for (String statement : seedUpserts) {
            Matcher literal = FIXED_DATETIME_LITERAL.matcher(statement);
            if (literal.find()) {
                violations.add("Fecha fija " + literal.group() + " en " + targetOf(statement));
            }
            Matcher assignment = TIMESTAMP_ASSIGNMENT.matcher(statement);
            while (assignment.find()) {
                String value = assignment.group(2).trim();
                if (!"$init_now".equals(value)) {
                    violations.add(assignment.group(1) + " de " + targetOf(statement) + " vale " + value);
                }
            }
        }
        return violations;
    }

    private static List<String> reExecutionViolations(String initScript) {
        List<String> violations = new ArrayList<>();
        Set<String> deleted = new LinkedHashSet<>();
        Set<String> upserted = new LinkedHashSet<>();
        for (String statement : seedStatements(initScript)) {
            Matcher matcher = RECORD_TARGET.matcher(statement);
            if (!matcher.find()) {
                continue;
            }
            String record = matcher.group(2);
            if ("DELETE".equalsIgnoreCase(matcher.group(1))) {
                deleted.add(record);
            } else if (!deleted.contains(record)) {
                violations.add("UPSERT sin DELETE previo para " + record);
            } else if (!upserted.add(record)) {
                violations.add("UPSERT repetido para " + record);
            }
        }
        return violations;
    }

    private static List<String> fixedDatetimeLiterals(String initScript) {
        List<String> matches = new ArrayList<>();
        for (String statement : statements(initScript)) {
            if (FIXED_DATETIME_LITERAL.matcher(statement).find()) {
                matches.add(statement);
            }
        }
        return matches;
    }

    private static List<String> seedUpsertTargets(String initScript) {
        List<String> targets = new ArrayList<>();
        for (String statement : seedUpserts(initScript)) {
            targets.add(targetOf(statement));
        }
        return targets;
    }

    private static Set<String> tablesDefining(String initScript, Pattern definition) {
        Set<String> tables = new LinkedHashSet<>();
        for (String statement : statements(initScript)) {
            if (definition.matcher(statement).find()) {
                tables.add(tableOf(statement));
            }
        }
        return tables;
    }

    private static List<String> seedUpserts(String initScript) {
        List<String> upserts = new ArrayList<>();
        for (String statement : seedStatements(initScript)) {
            if (statement.startsWith("UPSERT")) {
                upserts.add(statement);
            }
        }
        return upserts;
    }

    private static List<String> seedStatements(String initScript) {
        List<String> all = statements(initScript);
        int capture = initNowCaptureIndex(initScript);
        if (capture < 0) {
            return List.of();
        }
        return all.subList(capture + 1, all.size());
    }

    private static int initNowCaptureIndex(String initScript) {
        List<String> all = statements(initScript);
        for (int index = 0; index < all.size(); index++) {
            if (INIT_NOW_CAPTURE.matcher(all.get(index)).find()) {
                return index;
            }
        }
        return -1;
    }

    private static String tableOf(String statement) {
        Matcher matcher = FIELD_TABLE.matcher(statement);
        return matcher.find() ? matcher.group(1) : "<sin tabla>";
    }

    private static String targetOf(String statement) {
        Matcher matcher = RECORD_TARGET.matcher(statement);
        return matcher.find() ? matcher.group(2) : "<sin registro>";
    }

    private static List<String> statements(String initScript) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideSingleQuotes = false;
        boolean insideBackticks = false;
        for (char character : stripComments(initScript).toCharArray()) {
            if (character == '\'' && !insideBackticks) {
                insideSingleQuotes = !insideSingleQuotes;
                current.append(character);
            } else if (character == '`' && !insideSingleQuotes) {
                insideBackticks = !insideBackticks;
                current.append(character);
            } else if (character == ';' && !insideSingleQuotes && !insideBackticks) {
                addStatement(statements, current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        addStatement(statements, current.toString());
        return statements;
    }

    private static void addStatement(List<String> statements, String rawStatement) {
        String normalized = rawStatement.replaceAll("\\s+", " ").trim();
        if (!normalized.isEmpty()) {
            statements.add(normalized);
        }
    }

    private static String stripComments(String initScript) {
        return COMMENT.matcher(initScript).replaceAll("");
    }
}
