package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.ApplicationCatalogRepository;
import co.edu.uco.crosscutting.helpers.UtilText;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.crosscutting.helpers.UtilUUID.DEFAULT_UUID;

@Repository
public class ApplicationCatalogSurrealAdapter implements ApplicationCatalogRepository {

    private static final String SURREAL_TABLE_APPLICATION = "application";

    private final LoggingPort log;
    private final Surreal surreal;

    public ApplicationCatalogSurrealAdapter(final Surreal surreal, final LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(ApplicationCatalogSurrealAdapter.class);
        this.surreal = surreal;
    }

    @Override
    public List<ApplicationData> findAll() {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_APPLICATION + ";";
        final List<ApplicationData> applications = new ArrayList<>();
        try {
            final Response response = surreal.query(sql);
            if (isNullObject(response) || response.size() == 0) {
                return applications;
            }
            final Value statementResult = response.take(0);
            if (isNullObject(statementResult) || !statementResult.isArray()) {
                return applications;
            }
            final Array array = statementResult.getArray();
            for (int i = 0; i < array.len(); i++) {
                final Value item = array.get(i);
                if (!isNullObject(item) && item.isObject()) {
                    applications.add(toApplicationData(item.getObject()));
                }
            }
        } catch (final RuntimeException ex) {
            log.error("Error al consultar aplicaciones en SurrealDB: " + sql, ex);
            throw ex;
        }
        return applications;
    }

    private static ApplicationData toApplicationData(final Object obj) {
        final ApplicationData data = ApplicationData.build();
        data.setId(extractIdAsUUID(obj.get("id")));
        data.setName(stringOf(obj.get("name")));
        return data;
    }

    private static UUID extractIdAsUUID(final Value value) {
        if (isNullObject(value)) return DEFAULT_UUID;
        if (value.isRecordId()) {
            try {
                final String fullId = value.getRecordId().toString();
                final int separator = fullId.indexOf(':');
                if (separator > 0) {
                    final String idPart = cleanThingId(fullId.substring(separator + 1));
                    return UUID.fromString(idPart);
                }
            } catch (Exception e) {
                return DEFAULT_UUID;
            }
        }
        if (value.isString()) {
            try {
                final String raw = value.getString();
                final int separator = raw.indexOf(':');
                final String idPart = separator > 0 ? cleanThingId(raw.substring(separator + 1)) : cleanThingId(raw);
                return UUID.fromString(idPart);
            } catch (Exception e) {
                return DEFAULT_UUID;
            }
        }
        return DEFAULT_UUID;
    }

    private static String cleanThingId(String id) {
        id = UtilText.getDefault(id).trim();
        if (id.length() >= 2 && id.startsWith("`") && id.endsWith("`")) {
            id = id.substring(1, id.length() - 1);
        }
        if (id.length() >= 2 && id.startsWith("\u27E8") && id.endsWith("\u27E9")) {
            id = id.substring(1, id.length() - 1);
        }
        final int separator = id.indexOf(':');
        if (separator > 0) {
            id = id.substring(separator + 1);
        }
        return id.trim();
    }

    private static String stringOf(final Value value) {
        if (isNullObject(value) || value.isNull() || value.isNone()) return "";
        if (value.isString()) return value.getString();
        if (value.isUuid()) return value.getUuid().toString();
        return value.toString();
    }
}
