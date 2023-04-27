package fun.mortnon.service.login.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.ICaptcha;
import fun.mortnon.framework.properties.CaptchaProperties;
import fun.mortnon.service.login.CaptchaService;
import fun.mortnon.service.login.LoginFactory;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.login.model.MortnonCaptcha;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

/**
 * @author dongfangzan
 * @date 30.4.21 10:30 上午
 */
@Singleton
public class CaptchaServiceImpl implements CaptchaService {
    private static final String BASE64_IMAGE = "data:image/png;base64,";

    @Inject
    private CaptchaProperties captchaProperties;

    @Inject
    private LoginFactory loginFactory;

    @Override
    public boolean isEnabled() {
        return captchaProperties.isEnable();
    }

    @Override
    public MortnonCaptcha generateCaptcha() {
        MortnonCaptcha mortnonCaptcha = new MortnonCaptcha();
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(),
                captchaProperties.getLength(), 20);

        mortnonCaptcha.setCaptchaKey(UUID.randomUUID().toString());
        mortnonCaptcha.setCaptchaImage(toBase64(captcha));

        getStorageService().saveVerifyCode(mortnonCaptcha.getCaptchaKey(), captcha.getCode(), captchaProperties.getExpireSeconds());

        return mortnonCaptcha;
    }

    private String toBase64(ICaptcha captcha) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        captcha.write(outputStream);
        return BASE64_IMAGE + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }


    @Override
    public boolean verifyCaptcha(String captchaKey, String captchaCode) {

        if (StringUtils.isBlank(captchaKey) || StringUtils.isBlank(captchaCode)) {
            return false;
        }

        String verifyCode = getStorageService().getVerifyCode(captchaKey);
        if (StringUtils.isBlank(verifyCode)) {
            return false;
        }

        // 使用后就清除验证码
        getStorageService().deleteVerifyCode(captchaKey);

        return captchaCode.equalsIgnoreCase(verifyCode);
    }

    private LoginStorageService getStorageService() {
        return loginFactory.getConfigLoginStorageService();
    }
}
