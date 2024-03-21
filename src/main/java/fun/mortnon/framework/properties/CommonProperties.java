package fun.mortnon.framework.properties;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/2/29
 */
@Data
@Context
@ConfigurationProperties(value = "mortnon.common")
public class CommonProperties {
    /**
     * 语言
     */
    private String lang;

    /**
     * 为前端生成的 RSA 公钥有效时长（分钟）
     */
    private int rsaTtl;

    /**
     * 密码错误的检测周期（分钟）
     */
    private int checkDuration;
}
