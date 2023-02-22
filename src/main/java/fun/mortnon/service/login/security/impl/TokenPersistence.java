package fun.mortnon.service.login.security.impl;

import fun.mortnon.framework.properties.JwtProperties;
import fun.mortnon.service.login.LoginFactory;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.login.enums.LoginStorageType;
import fun.mortnon.service.login.security.TokenListener;
import io.lettuce.core.cluster.RedisClusterClient;
import io.micronaut.security.token.event.AccessTokenGeneratedEvent;
import io.micronaut.security.token.jwt.generator.AccessTokenConfigurationProperties;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存 access token
 *
 * @author dev2007
 * @date 2023/2/7
 */
@Singleton
@Slf4j
public class TokenPersistence implements TokenListener {
    @Inject
    private JwtProperties jwtProperties;

    @Inject
    private AccessTokenConfigurationProperties accessTokenConfigurationProperties;

    @Inject
    private LoginFactory loginFactory;

    @Override
    public void eventTask(AccessTokenGeneratedEvent event) {
        log.info("create token:{}", event.getSource());
        if (jwtProperties.isConsistency()) {
            Integer expiration = accessTokenConfigurationProperties.getExpiration();
            getStorageService().saveToken((String) event.getSource(), expiration);
        }
    }

    /**
     * token 是否存在
     *
     * @param accessToken
     * @return
     */
    public boolean isExists(String accessToken) {
        //如果配置 token 在持久化，进行检查；否则直接返回存在
        if (jwtProperties.isConsistency()) {
            return getStorageService().tokenIsExists(accessToken);
        }

        return true;
    }

    public void clearToken(String accessToken) {
        if (isExists(accessToken)) {
            getStorageService().deleteToken(accessToken);
        }
    }

    private LoginStorageService getStorageService() {
        return loginFactory.getConfigLoginStorageService();
    }
}
