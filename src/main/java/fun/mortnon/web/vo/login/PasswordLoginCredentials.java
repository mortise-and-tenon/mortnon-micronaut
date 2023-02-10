package fun.mortnon.web.vo.login;

import io.micronaut.security.authentication.UsernamePasswordCredentials;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author dongfangzan
 * @date 27.4.21 5:11 下午
 */
@Data
@Accessors(chain = true)
public class PasswordLoginCredentials extends UsernamePasswordCredentials {
    /**
     * 验证码token
     */
    private String verifyToken;

    /**
     * 验证码
     */
    private String code;

}
