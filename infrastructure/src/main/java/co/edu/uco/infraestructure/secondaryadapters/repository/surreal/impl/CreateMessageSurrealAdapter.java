package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.CreateMessageRepository;
import co.edu.uco.crosscutting.exceptions.BusinessException;
import co.edu.uco.crosscutting.helpers.UtilUUID;
import com.surrealdb.Surreal;
import org.springframework.stereotype.Repository;

import static co.edu.uco.crosscutting.helpers.UtilText.getDefault;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;

@Repository
public class CreateMessageSurrealAdapter implements CreateMessageRepository {

    private static final String TABLE_MESSAGE = "message";
    private static final String TABLE_MESSAGE_ENVIRONMENT = "message_environment";
    private static final String TABLE_APPLICATION = "application";
    private static final String TABLE_FUNCTIONALITY = "functionality";
    private static final String TABLE_ENVIRONMENT = "environment";
    private static final String TABLE_MESSAGE_TYPE = "message_type";
    private static final String TABLE_MESSAGE_CATEGORY = "message_category";
    private static final String TABLE_MESSAGE_STATE = "message_state";
    private static final String TABLE_MESSAGE_ENV_STATE = "message_environment_state";

    private final LoggingPort log;
    private final Surreal surreal;

    public CreateMessageSurrealAdapter(final Surreal surreal, final LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(CreateMessageSurrealAdapter.class);
        this.surreal = surreal;
    }

    @Override
    public void createMessage(MessageData message, String environmentId, String messageEnvironmentStateId) {
        String messageId = message.getId().toString();
        String messageEnvId = UtilUUID.getNewUUID().toString();

        String appId = message.getFunctionality().getApplication().getId().toString();
        String funcId = message.getFunctionality().getId().toString();
        String typeId = message.getType().getName();
        String categoryId = message.getCategory().getName();
        String statusId = message.getStatus().getName();

        String upsertMessageSql = "UPSERT " + toRecordId(TABLE_MESSAGE, messageId) + " CONTENT { "
                + "code: " + quote(message.getCode()) + ", "
                + "title: " + quote(message.getTitle()) + ", "
                + "content: " + quote(message.getContent()) + ", "
                + "type_id: " + toRecordId(TABLE_MESSAGE_TYPE, typeId) + ", "
                + "category_id: " + toRecordId(TABLE_MESSAGE_CATEGORY, categoryId) + ", "
                + "status_id: " + toRecordId(TABLE_MESSAGE_STATE, statusId) + ", "
                + "application_id: " + toRecordId(TABLE_APPLICATION, appId) + ", "
                + "application: " + quote(message.getApplication()) + ", "
                + "functionality_id: " + toRecordId(TABLE_FUNCTIONALITY, funcId)
                + " };";

        String upsertMessageEnvSql = "UPSERT " + toRecordId(TABLE_MESSAGE_ENVIRONMENT, messageEnvId) + " CONTENT { "
                + "message_id: " + toRecordId(TABLE_MESSAGE, messageId) + ", "
                + "environment_id: " + toRecordId(TABLE_ENVIRONMENT, environmentId) + ", "
                + "state_data_id: " + toRecordId(TABLE_MESSAGE_ENV_STATE, messageEnvironmentStateId)
                + " };";

        try {
            log.info("Executing SurrealQL upsert message: {}", upsertMessageSql);
            surreal.query(upsertMessageSql);

            log.info("Executing SurrealQL upsert message_environment: {}", upsertMessageEnvSql);
            surreal.query(upsertMessageEnvSql);
        } catch (Exception ex) {
            log.error("Error al persistir mensaje en SurrealDB", ex);
            throw BusinessException.buildTechnicalException("Error al persistir el mensaje en la base de datos SurrealDB", ex);
        }
    }

    private static String toRecordId(String table, String id) {
        String clean = getDefault(id).trim();
        if (clean.startsWith(table + ":")) {
            clean = clean.substring(table.length() + 1);
        }
        return recordIdLiteral(table, clean);
    }
}
