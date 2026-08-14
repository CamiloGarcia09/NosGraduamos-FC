package co.edu.uco.infraestructure.secondaryadapters.catalog;


import co.edu.uco.application.common.catalog.CatalogPortStaticRef;
import co.edu.uco.application.common.catalog.MessageCatalog;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisCatalogMessageAdapter implements CatalogPort {


    private final RedisTemplate<String, String> redisTemplate;

    public RedisCatalogMessageAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        CatalogPortStaticRef.set(this);
    }


    @Override
    public MessageCatalog getMessageModel(String key) {
        Map<Object, Object> entry = redisTemplate.opsForHash().entries(key);
        if (entry.isEmpty()) {
            return null;
        }
        return new MessageCatalog(
                (String) entry.get("code"),
                (String) entry.get("title"),
                (String) entry.get("content"),
                (String) entry.get("type"),
                (String) entry.get("category"));
    }

    @Override
    public String getMessage(String key) {
        return (String) redisTemplate.opsForHash().get(key, "content");
    }

    @Override
    public String getMessage(String key, String defaultMessage) {
        String message = (String) redisTemplate.opsForHash().get(key, "content");
        return message != null ? message : defaultMessage;
    }

    @Override
    public String getTitle(String key) {
        return (String) redisTemplate.opsForHash().get(key, "title");
    }

    @Override
    public void setMessage(String key, MessageCatalog message) {
        redisTemplate.opsForHash().putAll(key, Map.of(
                "code", message.code(),
                "title", message.title(),
                "content", message.content(),
                "type", message.type(),
                "category", message.category()));
    }

}