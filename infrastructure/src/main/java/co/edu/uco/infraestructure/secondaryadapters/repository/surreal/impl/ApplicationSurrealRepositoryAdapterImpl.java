package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.ApplicationRepository;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import com.surrealdb.Object;
import com.surrealdb.Surreal;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.datetime;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;

@Repository
public final class ApplicationSurrealRepositoryAdapterImpl extends SurrealCatalogSupport implements ApplicationRepository {

    private static final String SURREAL_TABLE_APPLICATION = "application";
    private static final String SURREAL_TABLE_LANGUAGE_BASE = "language_base";
    private static final String SURREAL_TABLE_APPLICATION_STATE = "application_state";

    public ApplicationSurrealRepositoryAdapterImpl(final Surreal surreal, final LoggingPortFactory loggerFactory) {
        super(surreal, loggerFactory.getLogger(ApplicationSurrealRepositoryAdapterImpl.class));
    }

    @Override
    public Optional<ApplicationData> findByName(final String name) {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_APPLICATION
                + " WHERE name = " + quote(name) + " LIMIT 1;";
        return queryOne(sql, "Error al consultar aplicación por nombre en SurrealDB: " + sql, this::toApplicationData);
    }

    @Override
    public boolean existsById(final String id) {
        final String sql = "SELECT * FROM " + recordIdLiteral(SURREAL_TABLE_APPLICATION, id) + " LIMIT 1;";
        return queryOne(sql, "Error al validar aplicación en SurrealDB: " + sql, obj -> obj).isPresent();
    }

    @Override
    public void create(final ApplicationData application, final String languageId, final LocalDateTime startDate,
                       final LocalDateTime endDate, final String stateId) {
        final String upsertSql = "UPSERT " + recordIdLiteral(SURREAL_TABLE_APPLICATION, application.getId().toString())
                + " CONTENT { "
                + "name: " + quote(application.getName()) + ", "
                + "language_id: " + recordIdLiteral(SURREAL_TABLE_LANGUAGE_BASE, languageId) + ", "
                + "start_date: " + datetime(startDate) + ", "
                + "end_date: " + datetime(endDate) + ", "
                + "state_id: " + recordIdLiteral(SURREAL_TABLE_APPLICATION_STATE, stateId)
                + " };";
        try {
            log.info("Executing SurrealQL upsert application: {}", upsertSql);
            surreal.query(upsertSql);
        } catch (Exception ex) {
            log.error("Error al persistir la aplicación en SurrealDB", ex);
            throw BusinessException.buildTechnicalException(
                    "Error al persistir la aplicación en la base de datos SurrealDB", ex, ExceptionLocation.INFRASTRUCTURE);
        }
    }

    private ApplicationData toApplicationData(final Object obj) {
        final ApplicationData data = ApplicationData.build();
        data.setId(extractIdAsUUID(obj.get("id")));
        data.setName(stringOf(obj.get("name")));
        return data;
    }
}