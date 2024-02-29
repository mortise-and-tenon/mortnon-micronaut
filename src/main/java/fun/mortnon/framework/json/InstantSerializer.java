package fun.mortnon.framework.json;

import fun.mortnon.framework.utils.DateTimeUtils;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Encoder;
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
    protected InstantSerializer(SerdeConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void serializeWithoutFormat(Encoder encoder, EncoderContext context, Instant value, Argument<? extends Instant> type) throws IOException {
        encoder.encodeString(DateTimeUtils.convertStr(value));
    }
}
