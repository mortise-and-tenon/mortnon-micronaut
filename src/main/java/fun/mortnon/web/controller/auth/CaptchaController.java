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

/**
 * 验证码
 *
 * @author dongfangzan
 * @date 30.4.21 9:54 上午
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/captcha")
public class CaptchaController {

    @Inject
    private CaptchaService captchaService;

    /**
     * 获取验证码
     */
    @Get
    public MortnonResult<MortnonCaptcha> captcha() {
        return ResultUtil.success(captchaService.generateCaptcha());
    }
}
