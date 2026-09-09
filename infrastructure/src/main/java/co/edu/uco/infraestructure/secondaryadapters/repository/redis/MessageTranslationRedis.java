package co.edu.uco.infraestructure.secondaryadapters.repository.redis;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import static co.edu.uco.crosscutting.helpers.UtilText.EMPTY;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.REDIS_HASH_TRANSLATION;

@Getter
@RedisHash(REDIS_HASH_TRANSLATION)
public final class MessageTranslationRedis {
    @Id
    private String id;
    private String messageCode;
    private String environmentId;
    private String sourceLanguage;
    private String targetLanguage;
    private String translatedTitle;
    private String translatedContent;
    private String provider;
    private String model;
    @TimeToLive
    private Long ttl;

    public MessageTranslationRedis() {
        setId(EMPTY);
        setMessageCode(EMPTY);
        setEnvironmentId(EMPTY);
        setSourceLanguage(EMPTY);
        setTargetLanguage(EMPTY);
        setTranslatedTitle(EMPTY);
        setTranslatedContent(EMPTY);
        setProvider(EMPTY);
        setModel(EMPTY);
    }

    public MessageTranslationRedis(String id, String messageCode, String environmentId, String sourceLanguage,
            String targetLanguage, String translatedTitle, String translatedContent, String provider, String model,
            Long ttl) {
        setId(id);
        setMessageCode(messageCode);
        setEnvironmentId(environmentId);
        setSourceLanguage(sourceLanguage);
        setTargetLanguage(targetLanguage);
        setTranslatedTitle(translatedTitle);
        setTranslatedContent(translatedContent);
        setProvider(provider);
        setModel(model);
        setTtl(ttl);
    }

    public void setId(String id) {
        this.id = trim(id);
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = trim(messageCode);
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = trim(environmentId);
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = trim(sourceLanguage);
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = trim(targetLanguage);
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = trim(translatedTitle);
    }

    public void setTranslatedContent(String translatedContent) {
        this.translatedContent = trim(translatedContent);
    }

    public void setProvider(String provider) {
        this.provider = trim(provider);
    }

    public void setModel(String model) {
        this.model = trim(model);
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}