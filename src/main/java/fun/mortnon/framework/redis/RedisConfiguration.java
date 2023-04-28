package fun.mortnon.framework.redis;

import io.lettuce.core.RedisURI;
import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/4/28
 */
@ConfigurationProperties(RedisConfiguration.REDIS_PREFIX)
@Data
public class RedisConfiguration {
    public static final String REDIS_PREFIX = "mortnon.redis";

    private RedisURI uri;

    public void setUri(String uri) {
        this.uri = RedisURI.create(uri);
    }
}
