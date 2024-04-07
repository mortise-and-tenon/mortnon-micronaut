package fun.mortnon.framework.properties;

import fun.mortnon.service.login.enums.LoginConstants;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/2/29
 */
@Data
@Context
@ConfigurationProperties(value = CommonProperties.PREFIX)
public class CommonProperties {
    public static final String PREFIX = "mortnon.common";

    /**
     * token 存储方式
     */
    private String storageType = LoginConstants.REDIS;

    /**
     * 语言
     */
    private String lang;

    /**
     * 为前端生成的 RSA 公钥有效时长（分钟）
     */
    private int rsaTtl;

    /**
     * 双因子验证码有效时长
     */
    private int doubleFactorTtl;

    /**
     * 公用 RSA 公钥
     */
    private String publicKey;

    /**
     * 公用 RSA 私钥，用于解密敏感数据传输，与前端公钥一对
     */
    private String secret;

    /**
     * 密码错误的检测周期（分钟）
     */
    private int checkDuration;
}
