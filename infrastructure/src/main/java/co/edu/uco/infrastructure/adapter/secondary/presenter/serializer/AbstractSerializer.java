package co.edu.uco.infrastructure.adapter.secondary.presenter.serializer;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.MEDIA_TYPE_DEFAULT;

public abstract class AbstractSerializer implements SerializerType {
    private final String contentType;
    protected AbstractSerializer(String contentType) {
        this.contentType = contentType;
    }
    @Override
    public String getSupportedContentType() {
        return contentType;
    }
    @Override
    public boolean supports(String mediaType) {
        return getSupportedContentType().equalsIgnoreCase(mediaType) ||
                mediaType.equals(MEDIA_TYPE_DEFAULT) && isDefault();
    }
    @Override
    public boolean isDefault() {
        return false;
    }
}