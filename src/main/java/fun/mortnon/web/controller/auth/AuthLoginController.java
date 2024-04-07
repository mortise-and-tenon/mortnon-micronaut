package fun.mortnon.web.controller.auth;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.utils.ResultBuilder;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.login.CaptchaService;
import fun.mortnon.service.login.LoginService;
import fun.mortnon.web.vo.login.DoubleFactor;
import fun.mortnon.web.vo.login.PasswordLoginCredentials;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.Authenticator;
import io.micronaut.security.endpoints.LoginController;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * 登录认证
 *
 * @author dev2007
 * @date 2023/2/10
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/login")
@Slf4j
public class AuthLoginController extends LoginController {
    /**
     * 响应内容处理工具
     */
    private ResultBuilder resultBuilder;

    private LoginService loginService;

    /**
     * @param authenticator  {@link Authenticator} collaborator
     * @param loginHandler   A collaborator which helps to build HTTP response depending on success or failure.
     * @param eventPublisher The application event publisher
     */
    public AuthLoginController(Authenticator authenticator, LoginHandler loginHandler,
                               ApplicationEventPublisher eventPublisher, ResultBuilder resultBuilder,
                               LoginService loginService) {
        super(authenticator, loginHandler, eventPublisher);
        this.resultBuilder = resultBuilder;
        this.loginService = loginService;
    }

    /**
     * 用户名、密码登录
     * <p>
     * 将按照配置 micronaut.security.authentication 选择对应的认证处理器进行认证处理
     * CookieAuthLoginHandler 或 JwtAuthLoginHandler
     *
     * @param passwordLoginCredentials 认证凭证
     * @param request                  Http 请求
     * @return
     */
    @OperationLog(LogConstants.LOGIN)
    @Post("/password")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @SingleResult
    public Publisher<MutableHttpResponse<?>> login(@Valid @Body PasswordLoginCredentials passwordLoginCredentials,
                                                   HttpRequest<?> request) {
        return Flux.from(authenticator.authenticate(request, passwordLoginCredentials))
                .map(authenticationResponse -> {
                    if (authenticationResponse.isAuthenticated() && authenticationResponse.getAuthentication().isPresent()) {
                        Authentication authentication = authenticationResponse.getAuthentication().get();
                        eventPublisher.publishEvent(new LoginSuccessfulEvent(authentication));
                        return loginHandler.loginSuccess(authentication, request);
                    } else {
                        eventPublisher.publishEvent(new LoginFailedEvent(authenticationResponse));
                        return loginHandler.loginFailed(authenticationResponse, request);
                    }
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(HttpResponse.unauthorized())));
    }

    /**
     * 生成双因子验证码
     *
     * @param doubleFactor
     * @return
     */
    @Post("/code")
    public MutableHttpResponse<MortnonResult> doubleFactorCode(@Body DoubleFactor doubleFactor) {
        loginService.generateDoubleFactorCode(doubleFactor);
        return HttpResponse.ok(MortnonResult.success());
    }
}
