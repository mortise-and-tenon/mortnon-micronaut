package fun.mortnon.web.controller.user.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import fun.mortnon.service.sys.vo.ProjectRoleDTO;
import lombok.Data;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/8
 */
@Data
public class CreateUserCommand {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 重复密码
     */
    private String repeatPassword;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 头像
     */
    private String head;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 分配的组织、角色
     */
    private List<ProjectRoleDTO> projectRoles;
}
