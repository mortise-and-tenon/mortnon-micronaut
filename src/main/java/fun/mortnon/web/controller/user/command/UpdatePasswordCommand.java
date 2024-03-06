package fun.mortnon.web.controller.user.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * @author dev2007
 * @date 2023/2/17
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class UpdatePasswordCommand {
    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank
    private String password;

    /**
     * 重复新密码
     */
    @NotBlank
    private String repeatPassword;
}
