package fun.mortnon.service.login.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.ICaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import fun.mortnon.framework.properties.CaptchaProperties;
import fun.mortnon.service.login.CaptchaService;
import fun.mortnon.service.login.LoginFactory;
import fun.mortnon.service.login.LoginStorageService;
import fun.mortnon.service.login.enums.LoginConstants;
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

        String code = "";
        String imgBase64 = "";

        if (captchaProperties.getType().equals(LoginConstants.CAPTCHA_TYPE_ARITHMETIC)) {
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


        mortnonCaptcha.setCaptchaKey(UUID.randomUUID().toString());
        mortnonCaptcha.setCaptchaImage(imgBase64);

        getStorageService().saveVerifyCode(mortnonCaptcha.getCaptchaKey(), code, captchaProperties.getExpireSeconds());

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

        if(captchaProperties.getType().equals(LoginConstants.CAPTCHA_TYPE_ARITHMETIC)){
            MathGenerator mathGenerator = new MathGenerator();
            return mathGenerator.verify(verifyCode,captchaCode);
        }

        return captchaCode.equalsIgnoreCase(verifyCode);
    }

    private LoginStorageService getStorageService() {
        return loginFactory.getConfigLoginStorageService();
    }
}
