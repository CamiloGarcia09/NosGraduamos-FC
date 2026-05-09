package co.edu.uco.core.application.facade.page.impl;

import co.edu.uco.core.application.dto.page.PageRequestDTO;
import co.edu.uco.core.application.facade.page.SimplePageFacade;
import co.edu.uco.core.application.mapper.SimplePageMapper;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.page.PageRequestDTOValidator;
import org.springframework.stereotype.Component;

@Component
public final class SimplePageFacadeImpl implements SimplePageFacade {
    private final SimplePageMapper simplePageMapper;
    private final PageRequestDTOValidator pageRequestValidator;
    SimplePageFacadeImpl(SimplePageMapper simplePageMapper, PageRequestDTOValidator pageRequestValidator) {
        this.simplePageMapper = simplePageMapper;
        this.pageRequestValidator = pageRequestValidator;
    }
    @Override
    public SimplePageRequest execute(PageRequestDTO pageDTO) {
        pageRequestValidator.validate(pageDTO);
        return simplePageMapper.toSimplePageRequest(pageDTO);
    }
}