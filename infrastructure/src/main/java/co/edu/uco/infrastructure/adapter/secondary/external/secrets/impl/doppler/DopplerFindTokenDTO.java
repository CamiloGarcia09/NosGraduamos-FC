package co.edu.uco.infrastructure.adapter.secondary.external.secrets.impl.doppler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;
import static co.edu.uco.utils.helper.UtilText.getDefault;

@AllArgsConstructor
@Getter
public final class DopplerFindTokenDTO {
    private final String name;
    private final String raw;
    @JsonCreator
    public DopplerFindTokenDTO(
            @JsonProperty(DOPPLER_DTO_NAME) String name,
            @JsonProperty(DOPPLER_DTO_VALUE) Map<String, Object> value) {
        this.name = name;
        this.raw = getDefault((String) value.get(DOPPLER_DTO_RAW));
    }
}