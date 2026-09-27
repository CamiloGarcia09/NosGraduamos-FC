package co.edu.uco.application.primaryports.dto.context;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class AvailableContextDTO {

    private CatalogItemDTO organization;
    private CatalogItemDTO application;
    private CatalogItemDTO environment;
}
