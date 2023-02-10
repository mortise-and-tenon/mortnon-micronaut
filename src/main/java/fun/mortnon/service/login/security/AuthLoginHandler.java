package fun.mortnon.service.login.security;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.vo.MortnonResult;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.token.jwt.bearer.AccessRefreshTokenLoginHandler;
import io.micronaut.security.token.jwt.generator.AccessRefreshTokenGenerator;
import jakarta.inject.Singleton;

/**
 * 认证后处理
 *
 * @author dev2007
 * @date 2023/2/10
 */
@Replaces(AccessRefreshTokenLoginHandler.class)
@Singleton
public class AuthLoginHandler extends AccessRefreshTokenLoginHandler {
    /**
     * @param accessRefreshTokenGenerator AccessRefresh Token generator
     */
    public AuthLoginHandler(AccessRefreshTokenGenerator accessRefreshTokenGenerator) {
        super(accessRefreshTokenGenerator);
    }

    @Override
    public MutableHttpResponse<?> loginFailed(AuthenticationResponse authenticationFailed, HttpRequest<?> request) {
        return HttpResponse.unauthorized().body(MortnonResult.fail(ErrorCodeEnum.INVALID_USERNAME_OR_PASSWORD));
    }
}
