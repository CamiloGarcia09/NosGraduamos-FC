package co.edu.uco.infrastructure.adapter.secondary.presenter.serializer;

import co.edu.uco.utils.exception.CrossWordsException;

public interface SerializerType {
    <T> String serialize(T data) throws CrossWordsException;
    String getSupportedContentType();
    boolean supports(String mediaType);
    boolean isDefault();
}