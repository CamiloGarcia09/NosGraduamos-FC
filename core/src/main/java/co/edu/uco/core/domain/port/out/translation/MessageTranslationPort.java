package co.edu.uco.core.domain.port.out.translation;

import co.edu.uco.core.domain.data.MessageTranslationRequestData;
import co.edu.uco.core.domain.data.MessageTranslationResponseData;

public interface MessageTranslationPort {
    MessageTranslationResponseData translate(MessageTranslationRequestData requestData);
}
