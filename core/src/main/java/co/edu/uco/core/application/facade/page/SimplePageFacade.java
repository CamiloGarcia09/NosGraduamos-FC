package co.edu.uco.core.application.facade.page;

import co.edu.uco.core.application.dto.page.PageRequestDTO;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;

public interface SimplePageFacade {
    SimplePageRequest execute(PageRequestDTO pageRequestDTO);
}