package co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl;

import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.repository.MessageCodeQueryPort;
import com.surrealdb.Surreal;
import org.springframework.stereotype.Repository;

import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infraestructure.secondaryadapters.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;

@Repository
public class MessageCodeSurrealAdapter extends SurrealCatalogSupport implements MessageCodeQueryPort {

    private static final String TABLE_MESSAGE = "message";
    private static final String TABLE_APPLICATION = "application";

    public MessageCodeSurrealAdapter(Surreal surreal, LoggingPortFactory loggerFactory) {
        super(surreal, loggerFactory.getLogger(MessageCodeSurrealAdapter.class));
    }

    @Override
    public boolean existsByCodeAndApplicationId(String code, String applicationId) {
        String sql = "SELECT id FROM " + TABLE_MESSAGE
                + " WHERE code = " + quote(code)
                + " AND application_id = " + recordIdLiteral(TABLE_APPLICATION, applicationId)
                + " LIMIT 1;";
        return queryOne(sql, "Error al validar el código del mensaje en SurrealDB: ", row -> row).isPresent();
    }
}
