package fun.mortnon.web.controller.auth;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.login.CaptchaService;
import fun.mortnon.service.sys.SysUserService;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.vo.login.PasswordLoginCredentials;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
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
import java.security.Principal;

/**
 * @author dev2007
 * @date 2023/2/10
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/login")
@Slf4j
public class MortnonLoginController extends LoginController {
    private CaptchaService captchaService;

    private SysUserService sysUserService;

    /**
     * @param authenticator  {@link Authenticator} collaborator
     * @param loginHandler   A collaborator which helps to build HTTP response depending on success or failure.
     * @param eventPublisher The application event publisher
     */
    public MortnonLoginController(Authenticator authenticator, LoginHandler loginHandler,
                                  ApplicationEventPublisher eventPublisher, CaptchaService captchaService,
                                  SysUserService sysUserService) {
        super(authenticator, loginHandler, eventPublisher);
        this.captchaService = captchaService;
        this.sysUserService = sysUserService;
    }

    /**
     * ??????
     *
     * @param passwordLoginCredentials
     * @param request
     * @return
     */
    @OperationLog(LogConstants.LOGIN)
    @Post("/password")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @SingleResult
    public Publisher<MutableHttpResponse<?>> login(@Valid @Body PasswordLoginCredentials passwordLoginCredentials,
                                                   HttpRequest<?> request) {
        if (!verifyCaptcha(passwordLoginCredentials)) {
            log.info("verify captcha fail.");
            return Mono.just(HttpResponse.unauthorized().body(MortnonResult.fail(ErrorCodeEnum.VERIFY_CODE_ERROR)));
        }

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
                }).switchIfEmpty(Mono.defer(() -> Mono.just(HttpResponse.unauthorized())));
    }

    /**
     * ?????????????????????????????????
     *
     * @param principal
     * @return
     */
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/user")
    public Mono<MortnonResult<SysUserDTO>> queryUser(@Nullable Principal principal) {
        return sysUserService.getUserByUsername(principal.getName())
                .map(SysUserDTO::convert)
                .map(MortnonResult::success);
    }

    /**
     * ???????????????
     *
     * @param passwordLoginCredentials
     * @return
     */
    private boolean verifyCaptcha(PasswordLoginCredentials passwordLoginCredentials) {
        //????????????????????????????????????????????????
        if (!captchaService.isEnabled()) {
            return true;
        }

        return captchaService.verifyCaptcha(passwordLoginCredentials.getVerifyToken(), passwordLoginCredentials.getCode());
    }
}
