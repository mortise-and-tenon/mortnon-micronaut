package fun.mortnon.web.controller.user.command;

import fun.mortnon.service.sys.vo.ProjectRoleDTO;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建用户数据
 *
 * @author dev2007
 * @date 2023/2/8
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class CreateUserCommand {
    /**
     * 用户名
     */
    @NotBlank
    private String userName;

    /**
     * 姓名
     */
    @NotBlank
    private String nickName;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 重复密码
     */
    @NotBlank
    private String repeatPassword;

    /**
     * 电子邮箱
     */
    @Email
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 性别
     */
    @NotNull
    private Integer sex;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 分配的组织、角色
     */
    private List<ProjectRoleDTO> projectRoles;
}
