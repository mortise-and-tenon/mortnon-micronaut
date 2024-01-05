package fun.mortnon.framework.json;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Encoder;
import io.micronaut.serde.Serializer;
import io.micronaut.serde.config.SerdeConfiguration;
import io.micronaut.serde.support.serdes.InstantSerde;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 用于 @Serializable
 * 将 Instant 序列化为 yyyy-MM-dd HH:mm:ss 格式的字符串
 *
 * @author dev2007
 * @date 2024/1/5
 */
@Replaces(InstantSerde.class)
@Singleton
public class InstantSerializer extends InstantSerde {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected InstantSerializer(SerdeConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void serializeWithoutFormat(Encoder encoder, EncoderContext context, Instant value, Argument<? extends Instant> type) throws IOException {
        encoder.encodeString(formatter.format(LocalDateTime.ofInstant(value, ZoneId.systemDefault())));
    }
}
