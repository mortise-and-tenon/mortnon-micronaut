package fun.mortnon.web.vo.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author dongfangzan
 * @date 27.4.21 5:11 下午
 */
@Data
@Accessors(chain = true)
@Serdeable
public class PasswordLoginCredentials extends UsernamePasswordCredentials {
    /**
     * 验证码token
     */
    @JsonProperty("verify_token")
    private String verifyToken;

    /**
     * 验证码
     */
    private String code;

}
