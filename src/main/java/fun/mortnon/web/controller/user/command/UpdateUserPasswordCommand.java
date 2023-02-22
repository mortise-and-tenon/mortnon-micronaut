package fun.mortnon.web.controller.user.command;

import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/17
 */
@Data
public class UpdateUserPasswordCommand {
    /**
     * 旧密码
     */
    private String oldPassword;
    /**
     * 新密码
     */
    private String password;

    /**
     * 重复新密码
     */
    private String repeatPassword;
}
