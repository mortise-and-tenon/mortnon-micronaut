package fun.mortnon.framework.properties;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.EachProperty;
import lombok.Data;

/**
 * 验证码相关基础配置
 * @author dongfangzan
 * @date 30.4.21 10:21 上午
 */
@Data
@Context
@ConfigurationProperties(value="mortnon.captcha")
public class CaptchaProperties {
    private long expireSeconds = 600L;

    private int width = 130;

    private int height = 48;

    private int length = 5;

}
