package fun.mortnon.service.login.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.ICaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import fun.mortnon.dal.sys.entity.config.CaptchaType;
import fun.mortnon.framework.properties.CaptchaProperties;
import fun.mortnon.service.login.CaptchaService;
import fun.mortnon.service.login.LoginFactory;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.login.enums.LoginConstants;
import fun.mortnon.service.login.model.MortnonCaptcha;
import fun.mortnon.service.sys.ConfigService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

/**
 * @author dongfangzan
 * @date 30.4.21 10:30 上午
 */
@Singleton
public class CaptchaServiceImpl implements CaptchaService {
    /**
     * 验证码图片base64数据前缀
     */
    private static final String BASE64_IMAGE = "data:image/png;base64,";

    /**
     * 验证码配置
     */
    @Inject
    private CaptchaProperties captchaProperties;

    /**
     * 登录工厂
     */
    @Inject
    private LoginFactory loginFactory;

    @Inject
    private ConfigService configService;

    @Override
    public Mono<Boolean> isEnabled() {
        return configService.queryConfig().map(config -> config.getCaptcha() != CaptchaType.DISABLE);
    }

    @Override
    public Mono<MortnonCaptcha> generateCaptcha() {
        MortnonCaptcha mortnonCaptcha = new MortnonCaptcha();

        return configService.queryConfig()
                .map(config -> {
                    CaptchaType captchaType = config.getCaptcha();

                    //未启用验证码
                    if (captchaType == CaptchaType.DISABLE) {
                        mortnonCaptcha.setEnabled(false);
                        return mortnonCaptcha;
                    }

                    String code = "";
                    String imgBase64 = "";

                    if (captchaType.equals(CaptchaType.ARITHMETIC)) {
                        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(),
                                captchaProperties.getLength(), 4);
                        shearCaptcha.setGenerator(new MathGenerator());
                        shearCaptcha.createCode();
                        code = shearCaptcha.getCode();
                        imgBase64 = toBase64(shearCaptcha);
                    } else {
                        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(),
                                captchaProperties.getLength(), 20);
                        code = captcha.getCode();
                        imgBase64 = toBase64(captcha);
                    }


                    mortnonCaptcha.setEnabled(true);
                    mortnonCaptcha.setKey(UUID.randomUUID().toString());
                    mortnonCaptcha.setImage(imgBase64);

                    getStorageService().saveVerifyCode(mortnonCaptcha.getKey(), code, captchaProperties.getExpireSeconds());

                    return mortnonCaptcha;
                });
    }

    private String toBase64(ICaptcha captcha) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        captcha.write(outputStream);
        return BASE64_IMAGE + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }


    @Override
    public Mono<Boolean> verifyCaptcha(String captchaKey, String captchaCode) {
        if (StringUtils.isBlank(captchaKey) || StringUtils.isBlank(captchaCode)) {
            return Mono.just(false);
        }

        String verifyCode = getStorageService().getVerifyCode(captchaKey);
        if (StringUtils.isBlank(verifyCode)) {
            return Mono.just(false);
        }

        // 使用后就清除验证码
        getStorageService().deleteVerifyCode(captchaKey);

        return configService.queryConfig()
                .map(config -> {
                    if (config.getCaptcha() == CaptchaType.ARITHMETIC) {
                        MathGenerator mathGenerator = new MathGenerator();
                        return mathGenerator.verify(verifyCode, captchaCode);
                    }
                    return captchaCode.equalsIgnoreCase(verifyCode);
                });
    }

    private LoginStorageService getStorageService() {
        return loginFactory.getConfigLoginStorageService();
    }
}
