package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.ActiveContextRepository;
import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import co.edu.uco.infraestructure.secondaryadapters.repository.data.ActiveContextSurrealMapper;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.ActiveContextSurrealModel;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.datetime;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;

@Repository
public class ActiveContextSurrealRepositoryAdapterImpl extends SurrealCatalogSupport
        implements ActiveContextRepository {

    private static final String ACTIVE_CONTEXT_TABLE = "active_context";
    private static final String EXTERNAL_IDENTITY_TABLE = "external_identity";
    private static final String ORGANIZATION_TABLE = "organization";
    private static final String APPLICATION_TABLE = "application";
    private static final String ENVIRONMENT_TABLE = "environment";
    private static final String FIND_LOG_MESSAGE = "Active context persistence read failed";
    private static final String SAVE_LOG_MESSAGE = "Active context persistence write failed";
    private static final String FIND_TECHNICAL_MESSAGE = "Unable to read active context from persistence";
    private static final String SAVE_TECHNICAL_MESSAGE = "Unable to write active context to persistence";

    private final ActiveContextSurrealMapper mapper;

    public ActiveContextSurrealRepositoryAdapterImpl(final Surreal surreal,
                                                      final LoggingPortFactory loggerFactory,
                                                      final ActiveContextSurrealMapper mapper) {
        super(surreal, loggerFactory.getLogger(ActiveContextSurrealRepositoryAdapterImpl.class));
        this.mapper = mapper;
    }

    @Override
    public Optional<ActiveContextEntity> findByExternalIdentityId(final UUID externalIdentityId) {
        final String sql = "SELECT * FROM "
                + recordIdLiteral(ACTIVE_CONTEXT_TABLE, externalIdentityId.toString()) + " LIMIT 1;";
        try {
            return queryOneSanitized(sql).map(mapper::mapperData);
        } catch (final RuntimeException exception) {
            log.error(FIND_LOG_MESSAGE);
            throw technicalException(FIND_TECHNICAL_MESSAGE, exception);
        }
    }

    @Override
    public void save(final ActiveContextEntity context) {
        final ActiveContextSurrealModel model = mapper.mapperModel(context);
        final String sql = "UPSERT "
                + recordIdLiteral(ACTIVE_CONTEXT_TABLE, model.getExternalIdentityId().toString())
                + " CONTENT { "
                + "external_identity_id: " + reference(EXTERNAL_IDENTITY_TABLE, model.getExternalIdentityId()) + ", "
                + "organization_id: " + reference(ORGANIZATION_TABLE, model.getOrganizationId()) + ", "
                + "application_id: " + reference(APPLICATION_TABLE, model.getApplicationId()) + ", "
                + "environment_id: " + reference(ENVIRONMENT_TABLE, model.getEnvironmentId()) + ", "
                + "updated_at: " + datetime(model.getUpdatedAt())
                + " };";
        try {
            surreal.query(sql);
        } catch (final RuntimeException exception) {
            log.error(SAVE_LOG_MESSAGE);
            throw technicalException(SAVE_TECHNICAL_MESSAGE, exception);
        }
    }

    private ActiveContextSurrealModel toModel(final Object document) {
        return new ActiveContextSurrealModel(
                extractIdAsUUID(document.get("id")),
                extractIdAsUUID(document.get("external_identity_id")),
                extractIdAsUUID(document.get("organization_id")),
                extractIdAsUUID(document.get("application_id")),
                extractIdAsUUID(document.get("environment_id")),
                dateTimeOf(document.get("updated_at")));
    }

    private Optional<ActiveContextSurrealModel> queryOneSanitized(final String sql) {
        final Response response = surreal.query(sql);
        if (response == null || response.size() == 0) {
            return Optional.empty();
        }
        final Value statement = response.take(0);
        if (statement == null || !statement.isArray()) {
            return Optional.empty();
        }
        final Array values = statement.getArray();
        if (values.len() == 0) {
            return Optional.empty();
        }
        final Value value = values.get(0);
        if (value == null || !value.isObject()) {
            return Optional.empty();
        }
        return Optional.of(toModel(value.getObject()));
    }

    private LocalDateTime dateTimeOf(final Value value) {
        if (value == null || !value.isDateTime()) {
            throw new IllegalArgumentException("Invalid active context timestamp");
        }
        return value.getDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    private String reference(final String table, final UUID id) {
        return recordIdLiteral(table, id.toString());
    }

    private BusinessException technicalException(final String message, final RuntimeException exception) {
        return BusinessException.buildTechnicalException(message, exception, ExceptionLocation.INFRASTRUCTURE);
    }
}
