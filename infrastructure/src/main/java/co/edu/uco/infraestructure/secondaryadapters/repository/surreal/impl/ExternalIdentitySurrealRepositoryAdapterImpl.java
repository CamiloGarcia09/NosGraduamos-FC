package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.ExternalIdentityRepository;
import co.edu.uco.application.usecase.domain.aggregate.entities.ExternalIdentityEntity;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.exceptions.enumeration.ExceptionLocation;
import co.edu.uco.infraestructure.secondaryadapters.repository.data.ExternalIdentitySurrealMapper;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.ExternalIdentitySurrealModel;
import com.surrealdb.Object;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;

@Repository
public class ExternalIdentitySurrealRepositoryAdapterImpl extends SurrealCatalogSupport
        implements ExternalIdentityRepository {

    private static final String SURREAL_TABLE_EXTERNAL_IDENTITY = "external_identity";
    private final ExternalIdentitySurrealMapper mapper;

    public ExternalIdentitySurrealRepositoryAdapterImpl(final Surreal surreal,
                                                        final LoggingPortFactory loggerFactory,
                                                        final ExternalIdentitySurrealMapper mapper) {
        super(surreal, loggerFactory.getLogger(ExternalIdentitySurrealRepositoryAdapterImpl.class));
        this.mapper = mapper;
    }

    @Override
    public Optional<ExternalIdentityEntity> findByIssuerAndSubject(final String issuer, final String subject) {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_EXTERNAL_IDENTITY
                + " WHERE issuer = " + quote(issuer)
                + " AND subject = " + quote(subject)
                + " LIMIT 1;";
        try {
            return queryOne(sql, "Error al consultar identidad externa en SurrealDB", this::toModel)
                    .map(mapper::mapperData);
        } catch (final BusinessException exception) {
            throw exception;
        } catch (final RuntimeException exception) {
            throw technicalException(
                    "Error al consultar la identidad externa en la base de datos SurrealDB", exception);
        }
    }

    @Override
    public void create(final ExternalIdentityEntity externalIdentity) {
        final ExternalIdentitySurrealModel model = mapper.mapperModel(externalIdentity);
        final String sql = "UPSERT "
                + recordIdLiteral(SURREAL_TABLE_EXTERNAL_IDENTITY, model.getId().toString())
                + " CONTENT { "
                + "issuer: " + quote(model.getIssuer()) + ", "
                + "subject: " + quote(model.getSubject()) + ", "
                + "email: " + quote(model.getEmail())
                + " };";
        executeWrite(sql,
                "Executing SurrealQL create external identity",
                "Error al crear la identidad externa en SurrealDB",
                "Error al crear la identidad externa en la base de datos SurrealDB");
    }

    @Override
    public void update(final ExternalIdentityEntity externalIdentity) {
        final ExternalIdentitySurrealModel model = mapper.mapperModel(externalIdentity);
        final String sql = "UPDATE "
                + recordIdLiteral(SURREAL_TABLE_EXTERNAL_IDENTITY, model.getId().toString())
                + " MERGE { "
                + "email: " + quote(model.getEmail()) + ", "
                + "updated_at: time::now()"
                + " };";
        executeWrite(sql,
                "Executing SurrealQL update external identity",
                "Error al actualizar la identidad externa en SurrealDB",
                "Error al actualizar la identidad externa en la base de datos SurrealDB");
    }

    private ExternalIdentitySurrealModel toModel(final Object document) {
        return new ExternalIdentitySurrealModel(
                extractIdAsUUID(document.get("id")),
                stringOf(document.get("issuer")),
                stringOf(document.get("subject")),
                nullableString(document.get("email")));
    }

    private String nullableString(final Value value) {
        return isNullObject(value) || value.isNull() || value.isNone() ? null : stringOf(value);
    }

    private void executeWrite(final String sql, final String infoMessage,
                              final String logErrorMessage, final String technicalMessage) {
        try {
            log.info(infoMessage);
            surreal.query(sql);
        } catch (final RuntimeException exception) {
            log.error(logErrorMessage, exception);
            throw technicalException(technicalMessage, exception);
        }
    }

    private BusinessException technicalException(final String message, final RuntimeException exception) {
        return BusinessException.buildTechnicalException(message, exception, ExceptionLocation.INFRASTRUCTURE);
    }
}
