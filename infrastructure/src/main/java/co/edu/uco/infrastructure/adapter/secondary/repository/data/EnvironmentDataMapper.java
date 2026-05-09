package co.edu.uco.infrastructure.adapter.secondary.repository.data;

import co.edu.uco.core.domain.data.ApplicationData;
import co.edu.uco.core.domain.data.EnvironmentData;
import co.edu.uco.infrastructure.adapter.secondary.repository.mongo.model.EnvironmentDocument;
import org.springframework.stereotype.Component;

import static co.edu.uco.utils.helper.UtilText.EMPTY;
import static co.edu.uco.utils.helper.UtilUUID.getStringFromUUID;
import static co.edu.uco.utils.helper.UtilUUID.getStringToUUID;

@Component
public final class EnvironmentDataMapper implements DataMapper<EnvironmentData, EnvironmentDocument> {
    @Override
    public EnvironmentData mapperData(EnvironmentDocument model) {
        return new EnvironmentData(getStringToUUID(model.getId()), model.getName(), ApplicationData.build(getStringToUUID(model.getApplication()), EMPTY));
    }
    @Override
    public EnvironmentDocument mapperModel(EnvironmentData data) {
        return new EnvironmentDocument(getStringFromUUID(data.getId()), data.getName(), getStringFromUUID(data.getApplication().getId()), EMPTY, EMPTY);
    }
}