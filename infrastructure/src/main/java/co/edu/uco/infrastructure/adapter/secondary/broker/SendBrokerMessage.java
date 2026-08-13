package co.edu.uco.infrastructure.adapter.secondary.broker;

import co.edu.uco.core.domain.domains.MessageCodeDomain;
import co.edu.uco.core.domain.port.out.broker.SendMessage;
import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.infrastructure.configuration.PulsarProperties;
import co.edu.uco.utils.exception.CrossWordsException;
import co.edu.uco.utils.mapper.json.UtilMapperJson;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class SendBrokerMessage implements SendMessage {
    private final PulsarClient client;
    private final UtilMapperJson utilMapperJson;
    private final PulsarProperties pulsarProperties;
    private final CatalogPort catalogPort;
    public SendBrokerMessage(PulsarClient client, UtilMapperJson utilMapperJson, PulsarProperties pulsarProperties, CatalogPort catalogPort) {
        this.client = client;
        this.utilMapperJson = utilMapperJson;
        this.pulsarProperties = pulsarProperties;
        this.catalogPort = catalogPort;
    }
    @Override
    public void execute(MessageCodeDomain messageDomain, HttpServletResponse response) {
        try (Producer<String> stringProducer = this.client
                .newProducer(Schema.STRING)
                .topic(pulsarProperties.getTopicName())
                .create()) {
            Optional<String> message = utilMapperJson.execute(messageDomain);
            if(message.isPresent()){
                stringProducer.send(message.get());
            }
        } catch (PulsarClientException ex) {
            throw CrossWordsException.buildInfrastructure(catalogPort.getMessage("TCH_002"), ex);
        }
    }
}