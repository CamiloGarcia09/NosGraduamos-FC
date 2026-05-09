package co.edu.uco.infrastructure.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.MICROSECONDS_PER_MILLISECOND;

@ReadingConverter
@Component
public final class LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {
    @Override
    public LocalDateTime convert(@NotNull Long source) {
        long millis = source / MICROSECONDS_PER_MILLISECOND;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
    }
}