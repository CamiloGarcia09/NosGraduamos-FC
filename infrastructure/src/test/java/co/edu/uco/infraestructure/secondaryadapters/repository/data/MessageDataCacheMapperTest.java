package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.secondaryports.entity.FunctionalityData;
import co.edu.uco.application.secondaryports.entity.MessageCategoryData;
import co.edu.uco.application.secondaryports.entity.MessageData;
import co.edu.uco.application.secondaryports.entity.MessageTypeData;
import co.edu.uco.infraestructure.secondaryadapters.repository.redis.MessageRedis;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageDataCacheMapperTest {

    private final MessageDataCacheMapper mapper = new MessageDataCacheMapper();

    @Test
    void mapperData_mapsRedisModelToMessageData() {
        UUID id = UUID.randomUUID();
        MessageRedis model = new MessageRedis(id, "CODE", "Title", "Content", "CATEGORY",
                "TYPE", "STATUS", "APP", "FUNC", "ENV");

        MessageData data = mapper.mapperData(model);

        assertThat(data.getId()).isEqualTo(id);
        assertThat(data.getCode()).isEqualTo("CODE");
        assertThat(data.getTitle()).isEqualTo("Title");
        assertThat(data.getContent()).isEqualTo("Content");
        assertThat(data.getType().getName()).isEqualTo("TYPE");
        assertThat(data.getCategory().getName()).isEqualTo("CATEGORY");
        assertThat(data.getApplication()).isEqualTo("APP");
        assertThat(data.getFunctionality().getName()).isEqualTo("FUNC");
    }

    @Test
    void mapperModel_mapsMessageDataToRedisModel() {
        UUID id = UUID.randomUUID();
        MessageData data = new MessageData(id, "CODE", "Title", "Content",
                MessageTypeData.build("TYPE"), MessageCategoryData.build("CATEGORY"),
                "APP", FunctionalityData.build("FUNC"));

        MessageRedis model = mapper.mapperModel(data);

        assertThat(model.getId()).isEqualTo(id);
        assertThat(model.getCode()).isEqualTo("CODE");
        assertThat(model.getTitle()).isEqualTo("Title");
        assertThat(model.getContent()).isEqualTo("Content");
        assertThat(model.getType()).isEqualTo("CATEGORY");
        assertThat(model.getCategory()).isEqualTo("TYPE");
        assertThat(model.getApplication()).isEqualTo("APP");
        assertThat(model.getFunctionality()).isEqualTo("FUNC");
        assertThat(model.getEnvironmentId()).isEmpty();
    }
}