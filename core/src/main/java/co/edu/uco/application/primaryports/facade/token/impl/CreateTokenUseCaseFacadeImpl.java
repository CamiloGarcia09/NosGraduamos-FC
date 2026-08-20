package co.edu.uco.application.primaryports.facade.token.impl;

import co.edu.uco.application.primaryports.dto.token.CreateTokenDTO;
import co.edu.uco.application.primaryports.dto.token.TokenDTO;
import co.edu.uco.application.primaryports.facade.token.CreateTokenUseCaseFacade;
import co.edu.uco.application.common.mapper.dto.impl.TokenDTOMapper;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.secondaryports.secret.CreateTokenSecretPort;
import co.edu.uco.application.secondaryports.secret.EncryptTokenPort;
import co.edu.uco.application.usecase.handling.HandlingCreateTokenPort;
import co.edu.uco.application.usecase.handling.HandlingRevokeTokenPort;
import co.edu.uco.application.usecase.validator.token.CreateTokenCompositeValidator;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.helpers.UtilPairKey;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static co.edu.uco.application.CrosswordsConstant.TOKEN_SECRET_IDENTIFIER;

import static co.edu.uco.application.CrosswordsConstant.TOKEN_STATE_ACTIVE_ID;
import static co.edu.uco.crosscutting.helpers.UtilDate.parseDate;
import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.crosscutting.helpers.UtilText.concatenateWithoutSeparator;
import static co.edu.uco.crosscutting.helpers.UtilText.stringToUpperCase;
import static co.edu.uco.crosscutting.helpers.UtilUUID.formatUUID;
import static co.edu.uco.crosscutting.helpers.UtilUUID.getStringToUUID;

@Component
public final class CreateTokenUseCaseFacadeImpl implements CreateTokenUseCaseFacade {

    private final CreateTokenCompositeValidator validator;
    private final HandlingRevokeTokenPort handlingRevokeTokenPort;
    private final EncryptTokenPort encrypt;
    private final TokenDTOMapper tokenDTOMapper;
    private final CreateTokenSecretPort createTokenSecretPort;
    private final HandlingCreateTokenPort handlingCreateTokenPort;
    private final LoggingPort log;
    private final CatalogPort catalogPort;

    public CreateTokenUseCaseFacadeImpl(HandlingCreateTokenPort handlingCreateTokenPort, TokenDTOMapper tokenDTOMapper,
                                        EncryptTokenPort encrypt, CreateTokenSecretPort createTokenSecretPort,
                                        CreateTokenCompositeValidator validator,
                                        HandlingRevokeTokenPort handlingRevokeTokenPort,
                                        LoggingPortFactory loggerFactory, CatalogPort catalogPort) {

        this.handlingCreateTokenPort = handlingCreateTokenPort;
        this.tokenDTOMapper = tokenDTOMapper;
        this.encrypt = encrypt;
        this.createTokenSecretPort = createTokenSecretPort;
        this.validator = validator;
        this.handlingRevokeTokenPort = handlingRevokeTokenPort;
        this.log = loggerFactory.getLogger(CreateTokenUseCaseFacadeImpl.class);
        this.catalogPort = catalogPort;
    }

    @Override
    public String execute(CreateTokenDTO createTokenDTO, String application) {

        validator.validate(createTokenDTO, application);
        handlingRevokeTokenPort.execute(createTokenDTO.getEnvironmentId(), TOKEN_STATE_ACTIVE_ID);

        var secretName = concatenateWithoutSeparator(
                TOKEN_SECRET_IDENTIFIER,
                stringToUpperCase(formatUUID(getStringToUUID(application))),
                stringToUpperCase(formatUUID(getStringToUUID(createTokenDTO.getEnvironmentId())))
        );

        var keyPairResponseDTO = encrypt.generateKeys();

        if(isNullObject(keyPairResponseDTO)){
            var message = catalogPort.getMessage(MessageCatalogCodeEnum.TCH_024.getCode());
            log.error(message);
            throw CrossWordsException.build(message);
        }

        try{
            var generateSignature = encrypt.generateSignature(secretName, keyPairResponseDTO.getPublicKey());

            var tokenDTO = new TokenDTO(
                    generateSignature,
                    secretName,
                    LocalDateTime.now(),
                    parseDate(createTokenDTO.getExpirationDate()),
                    getStringToUUID(createTokenDTO.getEnvironmentId())
            );

            createTokenSecretPort.execute(secretName, UtilPairKey.encodePrivateKey(keyPairResponseDTO.getPrivateKey()));
            handlingCreateTokenPort.createToken(tokenDTOMapper.mapperDomain(tokenDTO));
            return generateSignature;

        }catch (Exception e){
            var message = catalogPort.getMessage(MessageCatalogCodeEnum.TCH_025.getCode());
            log.error(message, e);
            throw CrossWordsException.build(message, e);
        }
    }
}