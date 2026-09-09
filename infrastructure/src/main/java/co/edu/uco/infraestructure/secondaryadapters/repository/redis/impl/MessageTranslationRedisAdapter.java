package co.edu.uco.infraestructure.secondaryadapters.repository.redis.impl;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.MessageTranslationResponseData;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.translation.MessageTranslationCachePort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.infraestructure.secondaryadapters.repository.redis.MessageTranslationRedis;
import co.edu.uco.infraestructure.secondaryadapters.repository.redis.MessageTranslationRedisRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;
import static co.edu.uco.crosscutting.helpers.UtilText.trim;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.TRANSLATION_CACHE_KEY_SEPARATOR;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.TRANSLATION_CACHE_REDIS_ADAPTER;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.TRANSLATION_CACHE_TTL_SECONDS;

@Component(TRANSLATION_CACHE_REDIS_ADAPTER)
public final class MessageTranslationRedisAdapter implements MessageTranslationCachePort {
    private final LoggingPort log;
    private final CatalogPort catalogPort;
    private final MessageTranslationRedisRepository repository;

    public MessageTranslationRedisAdapter(
            MessageTranslationRedisRepository repository,
            CatalogPort catalogPort,
            LoggingPortFactory loggerFactory
    ) {
        this.log = loggerFactory.getLogger(MessageTranslationRedisAdapter.class);
        this.catalogPort = catalogPort;
        this.repository = repository;
    }

    @Override
    public Optional<MessageTranslationResponseData> findTranslation(
            String messageCode,
            String environmentId,
            String sourceLanguage,
            String targetLanguage
    ) {
        if (isNullObject(messageCode) || isNullObject(environmentId)
                || isEmptyOrNull(sourceLanguage) || isEmptyOrNull(targetLanguage)) {
            return Optional.empty();
        }
        try {
            return repository.findById(buildKey(messageCode, environmentId, sourceLanguage, targetLanguage))
                    .map(this::toResponseData);
        } catch (DataAccessException ex) {
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_014.getCode()), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_015.getCode()), ex);
            return Optional.empty();
        }
    }

    @Override
    public void saveTranslation(
            String messageCode,
            String environmentId,
            String sourceLanguage,
            String targetLanguage,
            MessageTranslationResponseData translation
    ) {
        if (isNullObject(messageCode) || isNullObject(environmentId)
                || isEmptyOrNull(sourceLanguage) || isEmptyOrNull(targetLanguage)
                || isNullObject(translation)) {
            return;
        }
        try {
            var key = buildKey(messageCode, environmentId, sourceLanguage, targetLanguage);
            repository.save(new MessageTranslationRedis(
                    key,
                    messageCode,
                    environmentId,
                    sourceLanguage,
                    targetLanguage,
                    translation.getTranslatedTitle(),
                    translation.getTranslatedContent(),
                    translation.getProvider(),
                    translation.getModel(),
                    TRANSLATION_CACHE_TTL_SECONDS
            ));
        } catch (DataAccessException ex) {
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_014.getCode()), ex);
        } catch (Exception ex) {
            log.error(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_015.getCode()), ex);
        }
    }

    private String buildKey(String messageCode, String environmentId, String sourceLanguage, String targetLanguage) {
        return trim(messageCode) + TRANSLATION_CACHE_KEY_SEPARATOR
                + trim(environmentId) + TRANSLATION_CACHE_KEY_SEPARATOR
                + trim(sourceLanguage) + TRANSLATION_CACHE_KEY_SEPARATOR
                + trim(targetLanguage);
    }

    private MessageTranslationResponseData toResponseData(MessageTranslationRedis entity) {
        return MessageTranslationResponseData.create(
                entity.getTranslatedTitle(),
                entity.getTranslatedContent(),
                entity.getProvider(),
                entity.getModel(),
                0L
        );
    }
}