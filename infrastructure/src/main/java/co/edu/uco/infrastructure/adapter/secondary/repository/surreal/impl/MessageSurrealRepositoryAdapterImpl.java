package co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl;

import co.edu.uco.core.domain.data.MessageData;
import co.edu.uco.core.domain.data.FunctionalityData;
import co.edu.uco.core.domain.data.MessageCategoryData;
import co.edu.uco.core.domain.data.MessageTypeData;
import co.edu.uco.core.domain.data.StatusMessageData;
import co.edu.uco.core.domain.port.out.repository.DataBaseMessageRepository;
import co.edu.uco.core.domain.port.out.repository.SimplePage;
import com.surrealdb.Array;
import com.surrealdb.Object;
import com.surrealdb.Response;
import com.surrealdb.Surreal;
import com.surrealdb.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl.SurrealQLUtil.quote;
import static co.edu.uco.infrastructure.adapter.secondary.repository.surreal.impl.SurrealQLUtil.recordIdLiteral;

@Slf4j
@Repository
public class MessageSurrealRepositoryAdapterImpl implements DataBaseMessageRepository {

    private static final String SURREAL_TABLE_MESSAGE_DOCUMENT = "message_data_collection";
    private static final String SURREAL_TABLE_MESSAGE_ENVIRONMENT_READMODEL = "message_environment_readmodel";

    private final Surreal surreal;

    public MessageSurrealRepositoryAdapterImpl(final Surreal surreal) {
        this.surreal = surreal;
    }

    @Override
    public Optional<MessageData> findById(final String id) {
        final String messageId = "message:" + id;
        final String sql = "SELECT * FROM " + SURREAL_TABLE_MESSAGE_DOCUMENT
                + " WHERE message_id = " + quote(messageId)
                + " OR message_id = " + recordIdLiteral("message", id)
                + " LIMIT 1;";
        return findOneMessageDocument(sql);
    }

    @Override
    public SimplePage<MessageData> findMessagesByEnvironment(final String environmentId, final Pageable pageable) {
        long offset = pageable.getOffset();
        int pageSize = pageable.getPageSize();
        final String environmentFilter = environmentFilter(environmentId);
        
        final String countSql = "SELECT COUNT() as count FROM " + SURREAL_TABLE_MESSAGE_ENVIRONMENT_READMODEL
                + " WHERE " + environmentFilter + ";";
        
        final String dataSql = "SELECT * FROM " + SURREAL_TABLE_MESSAGE_ENVIRONMENT_READMODEL
                + " WHERE " + environmentFilter
                + " ORDER BY message.code ASC"
                + " LIMIT " + pageSize + " START " + offset + ";";

        try {
            long totalElements = getCount(countSql);
            List<MessageData> messages = findMultipleMessageDocuments(dataSql);
            
            int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);
            return SimplePage.of(
                    messages,
                    pageable.getPageNumber(),
                    pageSize,
                    totalElements,
                    totalPages
            );
        } catch (final RuntimeException ex) {
            log.error("Error finding messages by environment in SurrealDB. Environment ID: {}", environmentId, ex);
            throw ex;
        }
    }

    @Override
    public Optional<MessageData> findMessageByCodeAndEnvironment(final String code, final String environmentId) {
        final String sql = "SELECT * FROM " + SURREAL_TABLE_MESSAGE_ENVIRONMENT_READMODEL
                + " WHERE " + environmentFilter(environmentId)
                + " AND message.code = " + quote(code)
                + " LIMIT 1;";
        return findOneMessageDocument(sql);
    }

    private Optional<MessageData> findOneMessageDocument(final String sql) {
        try {
            final Response response = surreal.query(sql);
            if (response == null || response.size() == 0) {
                return Optional.empty();
            }
            final Value statementResult = response.take(0);
            if (statementResult == null || !statementResult.isArray()) {
                return Optional.empty();
            }
            final Array array = statementResult.getArray();
            if (array.len() == 0) {
                return Optional.empty();
            }
            final Value first = array.get(0);
            if (first == null || !first.isObject()) {
                return Optional.empty();
            }
            return Optional.of(toMessageDataFromDocument(first.getObject()));
        } catch (final RuntimeException ex) {
            log.error("Error querying SurrealDB. Query: {}", sql, ex);
            throw ex;
        }
    }

    private List<MessageData> findMultipleMessageDocuments(final String sql) {
        final List<MessageData> messages = new ArrayList<>();
        try {
            final Response response = surreal.query(sql);
            if (response == null || response.size() == 0) {
                return messages;
            }
            final Value statementResult = response.take(0);
            if (statementResult == null || !statementResult.isArray()) {
                return messages;
            }
            final Array array = statementResult.getArray();
            for (int i = 0; i < array.len(); i++) {
                final Value item = array.get(i);
                if (item != null && item.isObject()) {
                    messages.add(toMessageDataFromDocument(item.getObject()));
                }
            }
        } catch (final RuntimeException ex) {
            log.error("Error querying SurrealDB for multiple messages. Query: {}", sql, ex);
            throw ex;
        }
        return messages;
    }

    private long getCount(final String sql) {
        try {
            final Response response = surreal.query(sql);
            if (response == null || response.size() == 0) {
                return 0;
            }
            final Value statementResult = response.take(0);
            if (statementResult == null || !statementResult.isArray()) {
                return 0;
            }
            final Array array = statementResult.getArray();
            if (array.len() == 0) {
                return 0;
            }
            final Value first = array.get(0);
            if (first == null || !first.isObject()) {
                return 0;
            }
            final Object obj = first.getObject();
            final Value count = obj.get("count");
            if (count == null || count.isNull()) {
                return 0;
            }
            try {
                if (count.isString()) {
                    return Long.parseLong(count.getString());
                }
                return Long.parseLong(count.toPrettyString());
            } catch (final Exception e) {
                return 0;
            }
        } catch (final RuntimeException ex) {
            log.error("Error counting records in SurrealDB. Query: {}", sql, ex);
            return 0;
        }
    }

    private static String environmentFilter(final String environmentId) {
        final String environmentRecordId = "environment:" + environmentId;
        return "(environment_id = " + quote(environmentRecordId)
                + " OR environment_id = " + recordIdLiteral("environment", environmentId) + ")";
    }

    private static MessageData toMessageDataFromDocument(final Object obj) {
        final Object message = objectOf(obj.get("message")).orElse(obj);
        final MessageData data = MessageData.build();
        data.setId(extractIdAsUUID(message.get("id")));
        data.setCode(stringOf(message.get("code")));
        data.setTitle(stringOf(message.get("title")));
        data.setContent(stringOf(message.get("content")));
        data.setApplication(stringOf(message.get("application")));
        data.setType(MessageTypeData.build(nameOf(message.get("type"))));
        data.setCategory(MessageCategoryData.build(nameOf(message.get("category"))));
        data.setStatus(new StatusMessageData(UUID.randomUUID(), nameOf(message.get("status"))));
        data.setFunctionality(FunctionalityData.build(nameOf(message.get("functionality"))));
        return data;
    }

    private static Optional<Object> objectOf(final Value value) {
        if (value == null || !value.isObject()) {
            return Optional.empty();
        }
        return Optional.of(value.getObject());
    }

    private static String nameOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) {
            return "";
        }
        if (value.isObject()) {
            return stringOf(value.getObject().get("name"));
        }
        return stringOf(value);
    }

    private static UUID extractIdAsUUID(final Value value) {
        if (value == null) return UUID.randomUUID();
        if (value.isThing()) {
            try {
                return UUID.fromString(value.getThing().getId().toString());
            } catch (Exception e) {
                return UUID.randomUUID();
            }
        }
        if (value.isString()) {
            try {
                return UUID.fromString(value.getString());
            } catch (Exception e) {
                return UUID.randomUUID();
            }
        }
        return UUID.randomUUID();
    }

    private static String stringOf(final Value value) {
        if (value == null || value.isNull() || value.isNone()) return "";
        if (value.isString()) return value.getString();
        if (value.isUuid()) return value.getUuid().toString();
        return value.toPrettyString();
    }
}
