package co.edu.uco.infraestructure.secondaryadapters.catalog;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.common.catalog.MessageCatalog;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.crosscutting.catalog.MessageCatalogCode;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;
import static co.edu.uco.crosscutting.helpers.UtilText.EMPTY;

@Component
public class RedisCatalogMessageAdapter implements CatalogPort {

    private final LoggingPort log;
    private final RedisTemplate<String, String> redisTemplate;

    public RedisCatalogMessageAdapter(RedisTemplate<String, String> redisTemplate, LoggingPortFactory loggerFactory) {
        this.log = loggerFactory.getLogger(RedisCatalogMessageAdapter.class);
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        CatalogPortStaticRef.set(this);
    }

    @Override
    public MessageCatalog getMessageModel(String key) {
        if (isEmptyOrNull(trim(key))) {
            return null;
        }
        try {
            Map<Object, Object> entry = redisTemplate.opsForHash().entries(key);
            if (isNullObject(entry) || entry.isEmpty()) {
                return null;
            }
            return new MessageCatalog(
                    (String) entry.get("code"),
                    (String) entry.get("title"),
                    (String) entry.get("content"),
                    (String) entry.get("type"),
                    (String) entry.get("category"));
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage(MessageCatalogCode.TCH_051).formatted(key), ex);
            return null;
        }
    }

    @Override
    public String getMessage(String key) {
        if (isEmptyOrNull(trim(key))) {
            return EMPTY;
        }
        try {
            var value = (String) redisTemplate.opsForHash().get(key, "content");
            return isNullObject(value) ? key : value;
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage(MessageCatalogCode.TCH_052).formatted(key), ex);
            return key;
        }
    }

    @Override
    public String getMessage(String key, String defaultMessage) {
        if (isEmptyOrNull(trim(key))) {
            return defaultMessage;
        }
        try {
            String message = (String) redisTemplate.opsForHash().get(key, "content");
            return isNullObject(message) ? defaultMessage : message;
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage(MessageCatalogCode.TCH_052).formatted(key), ex);
            return defaultMessage;
        }
    }

    @Override
    public String getTitle(String key) {
        if (isEmptyOrNull(trim(key))) {
            return EMPTY;
        }
        try {
            var title = (String) redisTemplate.opsForHash().get(key, "title");
            return isNullObject(title) ? EMPTY : title;
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage(MessageCatalogCode.TCH_053).formatted(key), ex);
            return EMPTY;
        }
    }

    @Override
    public void setMessage(String key, MessageCatalog message) {
        if (isEmptyOrNull(trim(key)) || isNullObject(message)) {
            return;
        }
        try {
            redisTemplate.opsForHash().putAll(key, Map.of(
                    "code",     isNullObject(message.code())     ? EMPTY : message.code(),
                    "title",    isNullObject(message.title())    ? EMPTY : message.title(),
                    "content",  isNullObject(message.content())  ? EMPTY : message.content(),
                    "type",     isNullObject(message.type())     ? EMPTY : message.type(),
                    "category", isNullObject(message.category()) ? EMPTY : message.category()));
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage(MessageCatalogCode.TCH_054).formatted(key), ex);
        }
    }
}