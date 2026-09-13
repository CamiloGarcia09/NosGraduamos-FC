package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import com.surrealdb.Object;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;

@Repository
public class EnvironmentSurrealRepositoryAdapterImpl extends SurrealCatalogSupport implements EnvironmentRepository {

    private static final String SURREAL_TABLE_ENVIRONMENT = "environment";
    private static final String SURREAL_TABLE_APPLICATION = "application";
    private static final String SURREAL_TABLE_ENVIRONMENT_TYPE = "environment_type";
    private static final String SURREAL_TABLE_ENVIRONMENT_STATE = "environment_state";

    public EnvironmentSurrealRepositoryAdapterImpl(final Surreal surreal, final LoggingPortFactory loggerFactory) {
        super(surreal, loggerFactory.getLogger(EnvironmentSurrealRepositoryAdapterImpl.class));
    }

    @Override
    public boolean existsByNameAndApplicationId(final String name, final String applicationId) {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_ENVIRONMENT
                + " WHERE name = " + quote(name)
                + " AND application_id = " + recordIdLiteral(SURREAL_TABLE_APPLICATION, applicationId)
                + " LIMIT 1;";
        return queryOne(sql, "Error al validar entorno por nombre en SurrealDB: " + sql, obj -> obj).isPresent();
    }

    @Override
    public void create(final EnvironmentData environment, final String typeId, final String stateId) {
        final String upsertSql = "UPSERT " + recordIdLiteral(SURREAL_TABLE_ENVIRONMENT, environment.getId().toString())
                + " CONTENT { "
                + "name: " + quote(environment.getName()) + ", "
                + "application_id: " + recordIdLiteral(SURREAL_TABLE_APPLICATION, environment.getApplication().getId().toString()) + ", "
                + "type_id: " + recordIdLiteral(SURREAL_TABLE_ENVIRONMENT_TYPE, typeId) + ", "
                + "state_id: " + recordIdLiteral(SURREAL_TABLE_ENVIRONMENT_STATE, stateId)
                + " };";
        try {
            log.info("Executing SurrealQL upsert environment: {}", upsertSql);
            surreal.query(upsertSql);
        } catch (Exception ex) {
            log.error("Error al persistir el entorno en SurrealDB", ex);
            throw BusinessException.buildTechnicalException(
                    "Error al persistir el entorno en la base de datos SurrealDB", ex, ExceptionLocation.INFRASTRUCTURE);
        }
    }

    @Override
    public Optional<EnvironmentData> findById(final String id) {
        final String sql = "SELECT * FROM " + recordIdLiteral(SURREAL_TABLE_ENVIRONMENT, id) + " LIMIT 1;";
        return queryOne(sql, CatalogPortStaticRef.getMessage(MessageCatalogCodeEnum.TCH_056.getCode()).formatted(sql), this::toEnvironmentData);
    }

    private EnvironmentData toEnvironmentData(final Object obj) {
        final EnvironmentData data = EnvironmentData.build();
        data.setId(extractIdAsUUID(obj.get("id")));
        data.setName(stringOf(obj.get("name")));

        final ApplicationData app = ApplicationData.build();
        final Value appIdValue = obj.get("application_id");
        if (!isNullObject(appIdValue)) {
            app.setId(extractIdAsUUID(appIdValue));
        }
        data.setApplication(app);

        return data;
    }
}