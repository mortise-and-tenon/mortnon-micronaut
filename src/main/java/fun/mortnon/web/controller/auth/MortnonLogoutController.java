package fun.mortnon.web.controller.auth;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.event.LogoutEvent;
import io.micronaut.security.handlers.LogoutHandler;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dev2007
 * @date 2023/2/21
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/logout")
@Slf4j
public class MortnonLogoutController {
    @Inject
    private LogoutHandler logoutHandler;

    @Inject
    private ApplicationEventPublisher eventPublisher;

    @Get
    public MutableHttpResponse<?> indexGet(HttpRequest<?> request, @Nullable Authentication authentication) {
        if (authentication != null) {
            eventPublisher.publishEvent(new LogoutEvent(authentication));
        }
        return logoutHandler.logout(request);
    }
}
