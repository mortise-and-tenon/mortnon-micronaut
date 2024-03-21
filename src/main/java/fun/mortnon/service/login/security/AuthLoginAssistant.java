package fun.mortnon.service.login.security;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.utils.ResultBuilder;
import fun.mortnon.service.login.LoginService;
import fun.mortnon.service.login.vo.LockVO;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2024/3/15
 */
@Singleton
@Slf4j
public class AuthLoginAssistant {
    /**
     * 响应内容处理工具
     */
    @Inject
    private ResultBuilder resultBuilder;

    @Inject
    private LoginService loginService;

    public MutableHttpResponse<?> interceptLogin(HttpRequest<?> request) {
        try {
            long lockTime = loginService.interceptLogin(request);
            if (lockTime > 0) {
                LockVO lockVO = new LockVO();
                lockVO.setLockTime(lockTime);
                return HttpResponse.unauthorized().body(resultBuilder.buildWithData(ErrorCodeEnum.LOGIN_LOCK, lockVO));
            }
        } catch (Exception e) {
            log.warn("Exception in querying lock status：", e);
        }

        return null;
    }

    public MutableHttpResponse<?> responseFail(AuthenticationResponse authenticationFailed, HttpRequest<?> request) {
        if (authenticationFailed instanceof AuthenticationFailed) {
            AuthenticationFailed failed = (AuthenticationFailed) authenticationFailed;
            AuthenticationFailureReason reason = failed.getReason();
            switch (reason) {
                case CREDENTIALS_DO_NOT_MATCH:
                    MutableHttpResponse<?> interceptResponse = interceptLogin(request);
                    if (null != interceptResponse) {
                        return interceptResponse;
                    }
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
