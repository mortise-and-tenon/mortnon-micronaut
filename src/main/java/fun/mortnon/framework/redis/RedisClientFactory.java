package fun.mortnon.framework.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.configuration.lettuce.DefaultRedisConfiguration;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * @author dev2007
 * @date 2023/4/28
 */
@Requires(beans = DefaultRedisConfiguration.class)
@Singleton
@Factory
public class RedisClientFactory {

    @Bean
    public RedisCommands<String, String> redisCommands(StatefulRedisConnection<String, String> connection) {
        return connection.sync();
    }
}
