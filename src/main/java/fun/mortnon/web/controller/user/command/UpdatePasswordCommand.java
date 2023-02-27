package fun.mortnon.web.controller.user.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/17
 */
@Data
public class UpdatePasswordCommand {
    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户名字
     */
    @JsonProperty(value = "user_name")
    private String userName;

    /**
     * 旧密码
     */
    @JsonProperty(value = "old_password")
    private String oldPassword;
    /**
     * 新密码
     */
    private String password;

    /**
     * 重复新密码
     */
    @JsonProperty(value = "repeat_password")
    private String repeatPassword;
}
