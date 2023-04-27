package fun.mortnon.service.login.impl;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;

import fun.mortnon.framework.properties.CaptchaProperties;
import fun.mortnon.service.login.CaptchaService;
import fun.mortnon.service.login.LoginFactory;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.login.enums.LoginConstants;
import fun.mortnon.service.login.model.MortnonCaptcha;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author dongfangzan
 * @date 30.4.21 10:30 上午
 */
@Singleton
public class CaptchaServiceImpl implements CaptchaService {

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
        Captcha captcha = new SpecCaptcha();
        if (captchaProperties.getType().equals(LoginConstants.CAPTCHA_TYPE_DEFAULT)) {
            captcha = new SpecCaptcha();
            captcha.setLen(captchaProperties.getLength());
        }

        if (captchaProperties.getType().equals(LoginConstants.CAPTCHA_TYPE_GIF)) {
            captcha = new GifCaptcha();
            captcha.setLen(captchaProperties.getLength());
        }

        if (captchaProperties.getType().equals(LoginConstants.CAPTCHA_TYPE_CHINESE)) {
            captcha = new ChineseCaptcha();
            // 汉字4位
            captcha.setLen(4);
        }

        if (captchaProperties.getType().equals(LoginConstants.CAPTCHA_TYPE_ARITHMETIC)) {
            captcha = new ArithmeticCaptcha();
            // 算术2位即可
            captcha.setLen(2);
        }

        captcha.setWidth(captchaProperties.getWidth());
        captcha.setHeight(captchaProperties.getHeight());


        mortnonCaptcha.setCaptchaKey(UUID.randomUUID().toString());
        mortnonCaptcha.setCaptchaImage(captcha.toBase64());

        getStorageService().saveVerifyCode(mortnonCaptcha.getCaptchaKey(), captcha.text(), captchaProperties.getExpireSeconds());

        return mortnonCaptcha;
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
