package example.micronaut;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Controller("/captcha") // <1>
public class HelloController {
    private static final String BASE64_IMAGE = "data:image/png;base64,";

    @Get // <2>
    @Produces(MediaType.TEXT_PLAIN) // <3>
    public String index() {
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(100, 200, 4, 4);
        shearCaptcha.setGenerator(new MathGenerator());
        shearCaptcha.createCode();
        String code = shearCaptcha.getCode();
        System.out.println("code:" + code);
        return toBase64(shearCaptcha);
    }

    private String toBase64(ShearCaptcha captcha) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        captcha.write(outputStream);
        return BASE64_IMAGE + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
