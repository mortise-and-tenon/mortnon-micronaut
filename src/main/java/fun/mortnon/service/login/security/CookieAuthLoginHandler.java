package fun.mortnon.service.login.security;

import fun.mortnon.framework.vo.MortnonResult;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.authentication.Authentication;
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
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

/**
 * micronaut.security.authentication=cookie 对应认证处理器
 *
 * @author dev2007
 * @date 2023/3/7
 */
@Replaces(JwtCookieLoginHandler.class)
@Requires(property = SecurityConfigurationProperties.PREFIX + ".authentication", value = "cookie")
@Singleton
public class CookieAuthLoginHandler extends JwtCookieLoginHandler {
    @Inject
    private AuthLoginAssistant authLoginAssistant;

    public CookieAuthLoginHandler(RedirectService redirectService, RedirectConfiguration redirectConfiguration,
                                  AccessTokenCookieConfiguration accessTokenCookieConfiguration,
                                  RefreshTokenCookieConfiguration refreshTokenCookieConfiguration,
                                  AccessTokenConfiguration accessTokenConfiguration,
                                  AccessRefreshTokenGenerator accessRefreshTokenGenerator,
                                  @Nullable PriorToLoginPersistence priorToLoginPersistence) {
        super(redirectService, redirectConfiguration, accessTokenCookieConfiguration, refreshTokenCookieConfiguration,
                accessTokenConfiguration, accessRefreshTokenGenerator, priorToLoginPersistence);
    }

    /**
     * 认证失败的响应
     *
     * @param authenticationFailed Object encapsulates the Login failure
     * @param request              The {@link HttpRequest} being executed
     * @return
     */
    @Override
    public MutableHttpResponse<?> loginFailed(AuthenticationResponse authenticationFailed, HttpRequest<?> request) {
        return authLoginAssistant.responseFail(authenticationFailed, request);
    }

    @Override
    public MutableHttpResponse<?> loginSuccess(Authentication authentication, HttpRequest<?> request) {
        MutableHttpResponse<?> interceptResponse = authLoginAssistant.interceptLogin(request);
        if (null != interceptResponse) {
            return interceptResponse;
        }

        return super.loginSuccess(authentication, request);
    }

    /**
     * 认证成功的响应
     *
     * @param response The response
     * @param cookies  Cookies to be added to the response
     * @return
     */
    @Override
    public MutableHttpResponse<?> applyCookies(MutableHttpResponse<?> response, List<Cookie> cookies) {
        return super.applyCookies(response, cookies)
                .body(MortnonResult.success());
    }
}
