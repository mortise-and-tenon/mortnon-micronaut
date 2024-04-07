package fun.mortnon.web.vo.login;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * 认证数据
 *
 * @author dongfangzan
 * @date 27.4.21 5:11 下午
 */
@Data
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
public class PasswordLoginCredentials extends UsernamePasswordCredentials {
    /**
     * 验证码 key
     */
    private String verifyKey;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 双因子验证码
     */
    private String code;

}
