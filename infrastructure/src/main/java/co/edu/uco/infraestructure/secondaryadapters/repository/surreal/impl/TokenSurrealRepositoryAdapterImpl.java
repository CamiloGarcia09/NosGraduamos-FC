package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.helpers.UtilDate;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.TokenSurrealRepositoryAdapter;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.TokenSurrealModel;
import com.surrealdb.Object;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.datetime;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_CREATION_DATE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_ENVIRONMENT_ID;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_EXPIRATION_DATE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_SECRET_NAME;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.FIELD_STATE_ID;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.SURREAL_TABLE_TOKEN;

@Repository
public class TokenSurrealRepositoryAdapterImpl extends SurrealCatalogSupport implements TokenSurrealRepositoryAdapter {

    public TokenSurrealRepositoryAdapterImpl(final Surreal surreal, final LoggingPortFactory loggerFactory) {
        super(surreal, loggerFactory.getLogger(TokenSurrealRepositoryAdapterImpl.class));
    }

    @Override
    public TokenSurrealModel upsert(final TokenSurrealModel model) {
        final String sql = "UPSERT " + recordIdLiteral(SURREAL_TABLE_TOKEN, model.getId())
                + " CONTENT { "
                + FIELD_SECRET_NAME     + ": " + quote(model.getSecretName())          + ", "
                + FIELD_CREATION_DATE   + ": " + datetime(model.getCreationDate())     + ", "
                + FIELD_EXPIRATION_DATE + ": " + datetime(model.getExpirationDate())   + ", "
                + FIELD_ENVIRONMENT_ID  + ": " + quote(model.getEnvironmentId())       + ", "
                + FIELD_STATE_ID        + ": " + quote(model.getStateId())
                + " } RETURN AFTER;";

        try {
            surreal.query(sql);
            log.debug(CatalogPortStaticRef.getMessage(MessageCatalogCodeEnum.TCH_062.getCode()).formatted(model.getId()));
            return model;
        } catch (final RuntimeException ex) {
            log.error(CatalogPortStaticRef.getMessage(MessageCatalogCodeEnum.TCH_061.getCode()).formatted(sql), ex);
            throw ex;
        }
    }

    @Override
    public Optional<TokenSurrealModel> findTokenSurrealModelById(final String id) {
        final String sql = "SELECT * FROM " + recordIdLiteral(SURREAL_TABLE_TOKEN, id) + " LIMIT 1;";
        return findOne(sql);
    }

    @Override
    public Optional<TokenSurrealModel> findTokenSurrealModelByEnvironmentIdAndStateId(final String environmentId,
                                                                                       final String stateId) {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_TOKEN
                + " WHERE " + FIELD_ENVIRONMENT_ID + " = " + quote(environmentId)
                + " AND "  + FIELD_STATE_ID       + " = " + quote(stateId)
                + " LIMIT 1;";
        return findOne(sql);
    }

    private Optional<TokenSurrealModel> findOne(final String sql) {
        return queryOne(sql, "Error al consultar token en SurrealDB: " + sql, this::toModel);
    }

    private TokenSurrealModel toModel(final Object obj) {
        return new TokenSurrealModel(
                extractIdAsString(obj.get("id")),
                stringOf(obj.get(FIELD_SECRET_NAME)),
                dateOf(obj.get(FIELD_CREATION_DATE)),
                dateOf(obj.get(FIELD_EXPIRATION_DATE)),
                stringOf(obj.get(FIELD_ENVIRONMENT_ID)),
                stringOf(obj.get(FIELD_STATE_ID))
        );
    }

    private static String extractIdAsString(final Value value) {
        if (isNullObject(value)) return "";
        if (value.isRecordId()) return cleanThingId(value.getRecordId().getId().toString());
        if (value.isString()) return value.getString();
        return value.toString();
    }

    private static LocalDateTime dateOf(final Value value) {
        if (isNullObject(value) || value.isNull() || value.isNone()) return UtilDate.TIME;
        if (value.isDateTime()) return value.getDateTime().toLocalDateTime();
        return UtilDate.TIME;
    }
}