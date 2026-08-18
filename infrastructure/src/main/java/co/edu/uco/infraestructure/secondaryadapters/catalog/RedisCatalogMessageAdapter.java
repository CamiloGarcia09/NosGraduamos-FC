package co.edu.uco.infraestructure.secondaryadapters.catalog;

import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.common.catalog.MessageCatalog;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;

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
            if (entry == null || entry.isEmpty()) {
                return null;
            }
            return new MessageCatalog(
                    (String) entry.get("code"),
                    (String) entry.get("title"),
                    (String) entry.get("content"),
                    (String) entry.get("type"),
                    (String) entry.get("category"));
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage("TCH_051").formatted(key), ex);
            return null;
        }
    }

    @Override
    public String getMessage(String key) {
        if (isEmptyOrNull(trim(key))) {
            return "";
        }
        try {
            var value = (String) redisTemplate.opsForHash().get(key, "content");
            return value != null ? value : key;
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage("TCH_052").formatted(key), ex);
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
            return message != null ? message : defaultMessage;
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage("TCH_052").formatted(key), ex);
            return defaultMessage;
        }
    }

    @Override
    public String getTitle(String key) {
        if (isEmptyOrNull(trim(key))) {
            return "";
        }
        try {
            var title = (String) redisTemplate.opsForHash().get(key, "title");
            return title != null ? title : "";
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage("TCH_053").formatted(key), ex);
            return "";
        }
    }

    @Override
    public void setMessage(String key, MessageCatalog message) {
        if (isEmptyOrNull(trim(key)) || message == null) {
            return;
        }
        try {
            redisTemplate.opsForHash().putAll(key, Map.of(
                    "code", message.code() != null ? message.code() : "",
                    "title", message.title() != null ? message.title() : "",
                    "content", message.content() != null ? message.content() : "",
                    "type", message.type() != null ? message.type() : "",
                    "category", message.category() != null ? message.category() : ""));
        } catch (Exception ex) {
            log.error(CatalogPortStaticRef.getMessage("TCH_054").formatted(key), ex);
        }
    }
}