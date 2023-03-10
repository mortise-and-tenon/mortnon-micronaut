package fun.mortnon.service.login.security;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.vo.MortnonResult;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.config.RedirectConfiguration;
import io.micronaut.security.config.RedirectService;
import io.micronaut.security.config.SecurityConfigurationProperties;
import io.micronaut.security.errors.PriorToLoginPersistence;
import io.micronaut.security.token.jwt.cookie.AccessTokenCookieConfiguration;
import io.micronaut.security.token.jwt.cookie.JwtCookieLoginHandler;
import io.micronaut.security.token.jwt.cookie.RefreshTokenCookieConfiguration;
import io.micronaut.security.token.jwt.generator.AccessRefreshTokenGenerator;
import io.micronaut.security.token.jwt.generator.AccessTokenConfiguration;
import jakarta.inject.Singleton;

/**
 * @author dev2007
 * @date 2023/3/7
 */
@Replaces(JwtCookieLoginHandler.class)
@Requires(property = SecurityConfigurationProperties.PREFIX + ".authentication", value = "cookie")
@Singleton
public class CookieAuthLoginHandler extends JwtCookieLoginHandler {
    public CookieAuthLoginHandler(RedirectService redirectService, RedirectConfiguration redirectConfiguration, AccessTokenCookieConfiguration accessTokenCookieConfiguration, RefreshTokenCookieConfiguration refreshTokenCookieConfiguration, AccessTokenConfiguration accessTokenConfiguration, AccessRefreshTokenGenerator accessRefreshTokenGenerator, @Nullable PriorToLoginPersistence priorToLoginPersistence) {
        super(redirectService, redirectConfiguration, accessTokenCookieConfiguration, refreshTokenCookieConfiguration, accessTokenConfiguration, accessRefreshTokenGenerator, priorToLoginPersistence);
    }

    @Override
    public MutableHttpResponse<?> loginFailed(AuthenticationResponse authenticationFailed, HttpRequest<?> request) {
        return HttpResponse.unauthorized().body(MortnonResult.fail(ErrorCodeEnum.INVALID_USERNAME_OR_PASSWORD));
    }
}
