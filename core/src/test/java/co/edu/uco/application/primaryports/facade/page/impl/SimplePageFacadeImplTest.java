package co.edu.uco.application.primaryports.facade.page.impl;

import co.edu.uco.application.common.mapper.SimplePageMapper;
import co.edu.uco.application.primaryports.dto.page.PageRequestDTO;
import co.edu.uco.application.secondaryports.repository.SimplePageRequest;
import co.edu.uco.application.usecase.validator.page.PageRequestDTOValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimplePageFacadeImplTest {

    @Mock
    private SimplePageMapper simplePageMapper;
    @Mock
    private PageRequestDTOValidator pageRequestValidator;

    @InjectMocks
    private SimplePageFacadeImpl facade;

    @Test
    void execute_validatesAndMapsPageRequest() {
        PageRequestDTO dto = new PageRequestDTO();
        SimplePageRequest request = new SimplePageRequest();
        when(simplePageMapper.toSimplePageRequest(dto)).thenReturn(request);

        SimplePageRequest result = facade.execute(dto);

        assertThat(result).isSameAs(request);
        verify(pageRequestValidator).validate(dto);
    }
}