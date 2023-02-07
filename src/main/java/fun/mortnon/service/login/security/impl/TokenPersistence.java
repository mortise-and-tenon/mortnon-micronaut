package fun.mortnon.service.login.security.impl;

import fun.mortnon.framework.properties.JwtProperties;
import fun.mortnon.service.login.security.TokenListener;
import io.micronaut.security.token.event.AccessTokenGeneratedEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

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
    private List<String> storage = new ArrayList<>();

    @Inject
    private JwtProperties jwtProperties;

    @Override
    public void eventTask(AccessTokenGeneratedEvent event) {
        log.info("create token:{}", event.getSource());
        if (jwtProperties.isConsistency()) {
            storage.add((String) event.getSource());
        }
    }

    /**
     * token 是否存在
     *
     * @param accessToken
     * @return
     */
    public boolean isExists(String accessToken) {
        if (jwtProperties.isConsistency()) {
            return storage.contains(accessToken);
        }

        return true;
    }
}
