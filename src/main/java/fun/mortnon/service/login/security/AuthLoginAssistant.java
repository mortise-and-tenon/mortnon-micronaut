package fun.mortnon.service.login.security;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.utils.ResultBuilder;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2024/3/15
 */
@Singleton
public class AuthLoginAssistant {
    /**
     * 响应内容处理工具
     */
    @Inject
    private ResultBuilder resultBuilder;

    public MutableHttpResponse<?> responseFail(AuthenticationResponse authenticationFailed) {
        if (authenticationFailed instanceof AuthenticationFailed) {
            AuthenticationFailed failed = (AuthenticationFailed) authenticationFailed;
            AuthenticationFailureReason reason = failed.getReason();
            switch (reason) {
                case CREDENTIALS_DO_NOT_MATCH:
                    return HttpResponse.unauthorized().body(resultBuilder.build(ErrorCodeEnum.INVALID_USERNAME_OR_PASSWORD));
                case USER_DISABLED:
                    return HttpResponse.unauthorized().body(resultBuilder.build(ErrorCodeEnum.USER_FORBIDDEN));
                case CUSTOM:
                    return HttpResponse.unauthorized().body(resultBuilder.build(ErrorCodeEnum.VERIFY_CODE_ERROR));
                default:
                    return HttpResponse.unauthorized().body(resultBuilder.build(ErrorCodeEnum.USER_DATA_ERROR));

            }
        }
        return HttpResponse.unauthorized().body(resultBuilder.build(ErrorCodeEnum.INVALID_USERNAME_OR_PASSWORD));
    }
}
