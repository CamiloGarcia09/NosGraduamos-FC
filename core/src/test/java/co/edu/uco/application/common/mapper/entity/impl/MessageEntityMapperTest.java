package co.edu.uco.application.common.mapper.entity.impl;

import co.edu.uco.application.primaryports.dto.message.MessageDTO;
import co.edu.uco.application.secondaryports.entity.FunctionalityData;
import co.edu.uco.application.secondaryports.entity.MessageCategoryData;
import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.entity.MessageTypeData;
import co.edu.uco.application.usecase.domain.MessageDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageEntityMapperTest {

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MessageEntityMapper mapper;

    private MessageData messageData() {
        MessageData message = new MessageData();
        message.setCode("CODE");
        message.setTitle("Title");
        message.setContent("Content");
        message.setApplication("app");
        message.setType(MessageTypeData.build("info"));
        message.setCategory(MessageCategoryData.build("general"));
        message.setFunctionality(FunctionalityData.build("func"));
        return message;
    }

    @Test
    void mapperDomain_delegatesToModelMapper() {
        MessageData entity = messageData();
        MessageDomain domain = new MessageDomain();
        when(modelMapper.map(entity, MessageDomain.class)).thenReturn(domain);

        MessageDomain result = mapper.mapperDomain(entity);

        assertThat(result).isSameAs(domain);
        verify(modelMapper).map(entity, MessageDomain.class);
    }

    @Test
    void mapperData_delegatesToModelMapper() {
        MessageDomain domain = new MessageDomain();
        domain.setCode("CODE");
        MessageData entity = new MessageData();
        when(modelMapper.map(domain, MessageData.class)).thenReturn(entity);

        MessageData result = mapper.mapperData(domain);

        assertThat(result).isSameAs(entity);
        verify(modelMapper).map(domain, MessageData.class);
    }

    @Test
    void mapperDTO_buildsDtoFromEntityComposites() {
        MessageDTO dto = mapper.mapperDTO(messageData());

        assertThat(dto.code()).isEqualTo("CODE");
        assertThat(dto.title()).isEqualTo("Title");
        assertThat(dto.content()).isEqualTo("Content");
        assertThat(dto.type()).isEqualTo("info");
        assertThat(dto.category()).isEqualTo("general");
        assertThat(dto.application()).isEqualTo("app");
        assertThat(dto.functionality()).isEqualTo("func");
    }

    @Test
    void mapperDTO_normalizesEmptyCompositeNames() {
        MessageData message = new MessageData();
        message.setCode("CODE");
        message.setTitle("Title");
        message.setContent("Content");
        message.setApplication("app");

        MessageDTO dto = mapper.mapperDTO(message);

        assertThat(dto.type()).isEmpty();
        assertThat(dto.category()).isEmpty();
        assertThat(dto.functionality()).isEmpty();
    }
}