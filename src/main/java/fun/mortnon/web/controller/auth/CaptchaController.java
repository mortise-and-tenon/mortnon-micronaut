package fun.mortnon.web.controller.auth;


import fun.mortnon.framework.utils.ResultUtil;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.service.login.CaptchaService;
import fun.mortnon.service.login.model.MortnonCaptcha;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

/**
 * 验证码
 *
 * @author dongfangzan
 * @date 30.4.21 9:54 上午
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/captcha")
public class CaptchaController {

    /**
     * 验证码服务
     */
    @Inject
    private CaptchaService captchaService;

    /**
     * 获取验证码
     */
    @Get
    public Mono<MortnonResult<MortnonCaptcha>> captcha() {
        return captchaService.generateCaptcha().map(MortnonResult::success);
    }
}
