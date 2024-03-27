package fun.mortnon.framework.properties;

import fun.mortnon.service.login.enums.LoginConstants;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.EachProperty;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/7
 */
@Data
@Context
@ConfigurationProperties(value = "mortnon.jwt")
public class JwtProperties {
    /**
     * token 一致性
     * 如果为 true，使用的 token 必须在服务端存在；
     * 如果为 false，使用的 token 只要算法校验通过即可使用
     */
    private boolean consistency = false;
}
