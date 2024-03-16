package fun.mortnon.service.login;

import fun.mortnon.service.login.model.MortnonCaptcha;
import reactor.core.publisher.Mono;

/**
 * 验证码服务
 *
 * @author dongfangzan
 * @date 30.4.21 10:26 上午
 */
public interface CaptchaService {
    /**
     * 是否启用验证码
     *
     * @return
     */
    Mono<Boolean> isEnabled();

    /**
     * 生成验证码
     *
     * @return
     */
    Mono<MortnonCaptcha> generateCaptcha();

    /**
     * 验证码是否验证通过
     *
     * @param captchaKey  验证码key
     * @param captchaCode 验证码值
     * @return true-验证通过，false-不通过
     */
    Mono<Boolean> verifyCaptcha(String captchaKey, String captchaCode);
}
