package fun.mortnon.service.login.security;

import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.security.token.event.AccessTokenGeneratedEvent;

/**
 * AccessToken
 *
 * @author dev2007
 * @date 2023/2/7
 */
public interface TokenListener {
    /**
     * 获取到生成 token 后的相应操作
     *
     * @param event
     */
    @EventListener
    void eventTask(AccessTokenGeneratedEvent event);
}
