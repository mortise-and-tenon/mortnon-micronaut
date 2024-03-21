package fun.mortnon.service.login.security;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.config.SecurityConfigurationProperties;
import io.micronaut.security.token.jwt.bearer.AccessRefreshTokenLoginHandler;
import io.micronaut.security.token.jwt.generator.AccessRefreshTokenGenerator;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * micronaut.security.authentication=bearer 对应认证处理器
 *
 * @author dev2007
 * @date 2023/2/10
 */
@Replaces(AccessRefreshTokenLoginHandler.class)
@Requires(property = SecurityConfigurationProperties.PREFIX + ".authentication", value = "bearer")
@Singleton
public class JwtAuthLoginHandler extends AccessRefreshTokenLoginHandler {
    @Inject
    private AuthLoginAssistant authLoginAssistant;

    /**
     * @param accessRefreshTokenGenerator AccessRefresh Token generator
     */
    public JwtAuthLoginHandler(AccessRefreshTokenGenerator accessRefreshTokenGenerator) {
        super(accessRefreshTokenGenerator);
    }

    @Override
    public MutableHttpResponse<?> loginSuccess(Authentication authentication, HttpRequest<?> request) {
        MutableHttpResponse<?> interceptResponse = authLoginAssistant.interceptLogin(request);
        if (null != interceptResponse) {
            return interceptResponse;
        }

        return super.loginSuccess(authentication, request);
    }

    @Override
    public MutableHttpResponse<?> loginFailed(AuthenticationResponse authenticationFailed, HttpRequest<?> request) {
        return authLoginAssistant.responseFail(authenticationFailed, request);
    }
}
