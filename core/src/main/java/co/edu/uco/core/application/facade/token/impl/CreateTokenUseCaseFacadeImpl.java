package co.edu.uco.core.application.facade.token.impl;

import co.edu.uco.core.application.dto.token.CreateTokenDTO;
import co.edu.uco.core.application.dto.token.TokenDTO;
import co.edu.uco.core.application.facade.token.CreateTokenUseCaseFacade;
import co.edu.uco.core.application.mapper.dto.impl.TokenDTOMapper;
import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.core.domain.port.out.logging.LoggingPort;
import co.edu.uco.core.domain.port.out.logging.LoggingPortFactory;
import co.edu.uco.core.domain.port.out.secret.CreateTokenSecretPort;
import co.edu.uco.core.domain.port.out.secret.EncryptTokenPort;
import co.edu.uco.core.domain.usecase.handling.HandlingCreateTokenPort;
import co.edu.uco.core.domain.usecase.handling.HandlingRevokeTokenPort;
import co.edu.uco.core.domain.validator.token.CreateTokenCompositeValidator;
import co.edu.uco.utils.exception.CrossWordsException;
import co.edu.uco.utils.helper.UtilPairKey;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static co.edu.uco.core.CrosswordsConstant.TOKEN_SECRET_IDENTIFIER;

import static co.edu.uco.core.CrosswordsConstant.TOKEN_STATE_ACTIVE_ID;
import static co.edu.uco.utils.helper.UtilDate.parseDate;
import static co.edu.uco.utils.helper.UtilObject.isNullObject;
import static co.edu.uco.utils.helper.UtilText.concatenateWithoutSeparator;
import static co.edu.uco.utils.helper.UtilText.stringToUpperCase;
import static co.edu.uco.utils.helper.UtilUUID.formatUUID;
import static co.edu.uco.utils.helper.UtilUUID.getStringToUUID;

@Component
public final class CreateTokenUseCaseFacadeImpl implements CreateTokenUseCaseFacade {

    private final HandlingCreateTokenPort handlingCreateTokenPort;
    private final TokenDTOMapper tokenDTOMapper;
    private final CreateTokenSecretPort createTokenSecretPort;
    private final CreateTokenCompositeValidator validator;
    private final EncryptTokenPort encrypt;
    private final HandlingRevokeTokenPort handlingRevokeTokenPort;
    private final LoggingPort log;
    private final CatalogPort catalogPort;

    public CreateTokenUseCaseFacadeImpl(
            HandlingCreateTokenPort handlingCreateTokenPort,
            TokenDTOMapper tokenDTOMapper,
            EncryptTokenPort encrypt,
            CreateTokenSecretPort createTokenSecretPort,
            CreateTokenCompositeValidator validator,
            HandlingRevokeTokenPort handlingRevokeTokenPort,
            LoggingPortFactory loggerFactory,
            CatalogPort catalogPort
    ) {
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
    public String execute(
            CreateTokenDTO createTokenDTO,
            String application
    ) {
        validator.validate(createTokenDTO, application);
        handlingRevokeTokenPort.execute(createTokenDTO.getEnvironmentId(), TOKEN_STATE_ACTIVE_ID);
        var secretName = concatenateWithoutSeparator(
                TOKEN_SECRET_IDENTIFIER,
                stringToUpperCase(formatUUID(getStringToUUID(application))),
                stringToUpperCase(formatUUID(getStringToUUID(createTokenDTO.getEnvironmentId())))
        );

        var keyPairResponseDTO = encrypt.generateKeys();

        if(isNullObject(keyPairResponseDTO)){
            var message = catalogPort.getMessage("TCH_024");
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
            var message = catalogPort.getMessage("TCH_025");
            log.error(message, e);
            throw CrossWordsException.build(message, e);
        }
    }
}