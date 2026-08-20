package co.edu.uco.infraestructure.secondaryadapters.broker;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.logging.LoggingPort;
import co.edu.uco.application.secondaryports.logging.LoggingPortFactory;
import co.edu.uco.application.usecase.domain.MessageCodeDomain;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import co.edu.uco.crosscutting.helpers.json.UtilMapperJson;
import co.edu.uco.infraestructure.config.PulsarProperties;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendBrokerMessageTest {

    @Mock
    private PulsarClient client;
    @Mock
    private UtilMapperJson utilMapperJson;
    @Mock
    private CatalogPort catalogPort;
    @Mock
    private LoggingPortFactory loggerFactory;
    @Mock
    private LoggingPort log;
    @Mock
    private ProducerBuilder<String> producerBuilder;
    @Mock
    private Producer<String> producer;
    @Mock
    private HttpServletResponse response;

    private final PulsarProperties pulsarProperties = new PulsarProperties();
    private SendBrokerMessage adapter;

    @BeforeEach
    void setUp() {
        when(loggerFactory.getLogger(SendBrokerMessage.class)).thenReturn(log);
        pulsarProperties.setTopicName("topic-name");
        adapter = new SendBrokerMessage(client, utilMapperJson, pulsarProperties, catalogPort, loggerFactory);
    }

    private MessageCodeDomain messageDomain() {
        return MessageCodeDomain.create("MSG-001");
    }

    private void stubProducerChain() throws Exception {
        when(client.newProducer(Schema.STRING)).thenReturn(producerBuilder);
        when(producerBuilder.topic("topic-name")).thenReturn(producerBuilder);
        when(producerBuilder.create()).thenReturn(producer);
    }

    @Test
    void execute_throws_whenMessageIsNull() {
        when(catalogPort.getMessage("TCH_038")).thenReturn("tech");
        when(catalogPort.getMessage("FUN_023")).thenReturn("user");

        assertThatThrownBy(() -> adapter.execute(null, response))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> {
                    CrossWordsException cwe = (CrossWordsException) ex;
                    assertThat(cwe.getTechnicalMessage()).isEqualTo("tech");
                    assertThat(cwe.getUserMessage()).isEqualTo("user");
                });
    }

    @Test
    void execute_throws_whenClientIsNull() {
        SendBrokerMessage noClient = new SendBrokerMessage(null, utilMapperJson, pulsarProperties, catalogPort, loggerFactory);
        when(catalogPort.getMessage("TCH_002")).thenReturn("tech");
        when(catalogPort.getMessage("FUN_023")).thenReturn("user");

        assertThatThrownBy(() -> noClient.execute(messageDomain(), response))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> {
                    CrossWordsException cwe = (CrossWordsException) ex;
                    assertThat(cwe.getTechnicalMessage()).isEqualTo("tech");
                    assertThat(cwe.getUserMessage()).isEqualTo("user");
                });
    }

    @Test
    void execute_sendsMessage_whenSerializationSucceeds() throws Exception {
        stubProducerChain();
        when(utilMapperJson.execute(any(MessageCodeDomain.class))).thenReturn(Optional.of("{\"code\":\"MSG-001\"}"));

        adapter.execute(messageDomain(), response);

        verify(producer).send("{\"code\":\"MSG-001\"}");
        verify(producer).close();
    }

    @Test
    void execute_throws_whenSerializationEmpty() throws Exception {
        stubProducerChain();
        when(utilMapperJson.execute(any(MessageCodeDomain.class))).thenReturn(Optional.empty());
        when(catalogPort.getMessage("TCH_039")).thenReturn("tech");
        when(catalogPort.getMessage("FUN_023")).thenReturn("user");

        assertThatThrownBy(() -> adapter.execute(messageDomain(), response))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("tech"));
    }

    @Test
    void execute_throws_whenProducerCreationFails() throws Exception {
        when(client.newProducer(Schema.STRING)).thenReturn(producerBuilder);
        when(producerBuilder.topic("topic-name")).thenReturn(producerBuilder);
        when(producerBuilder.create()).thenThrow(new PulsarClientException("pulsar down"));
        when(catalogPort.getMessage("TCH_002")).thenReturn("tech");
        when(catalogPort.getMessage("FUN_023")).thenReturn("user");

        assertThatThrownBy(() -> adapter.execute(messageDomain(), response))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("tech"));
    }

    @Test
    void execute_throws_whenUnexpectedError() throws Exception {
        stubProducerChain();
        when(utilMapperJson.execute(any(MessageCodeDomain.class))).thenThrow(new IllegalStateException("boom"));
        when(catalogPort.getMessage("TCH_002")).thenReturn("tech");
        when(catalogPort.getMessage("FUN_023")).thenReturn("user");

        assertThatThrownBy(() -> adapter.execute(messageDomain(), response))
                .isInstanceOf(CrossWordsException.class)
                .satisfies(ex -> assertThat(((CrossWordsException) ex).getTechnicalMessage()).isEqualTo("tech"));
    }
}