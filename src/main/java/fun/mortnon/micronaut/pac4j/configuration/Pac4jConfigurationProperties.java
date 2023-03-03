package fun.mortnon.micronaut.pac4j.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/3/2
 */
@Data
@ConfigurationProperties(value = Pac4jConfigurationProperties.PREFIX)
public class Pac4jConfigurationProperties {
    public static final String PREFIX = "mortnon.pac4j";

    public static final String DEFAULT_LOGIN_URI = "/sso/login";
    public static final String DEFAULT_CALLBACK_URI = "/callback";


    /**
     * 是否全局 URI 都会触发 SSO
     * 如果为 false，则只会由 loginUrl 触发
     */
    private boolean global = false;
    /**
     * SSO 认证触发地址
     */
    private String loginUri = DEFAULT_LOGIN_URI;

    /**
     * SSO 认证回调地址
     */
    private String callbackUri = DEFAULT_CALLBACK_URI;


}
