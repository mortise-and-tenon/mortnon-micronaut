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
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("nick_name")
    private String nickName;
    private String password;
    @JsonProperty("repeat_password")
    private String repeatPassword;
    private String email;
    private String phone;
    private String head;
    private Integer sex;

    @JsonProperty(value = "project_roles")
    private List<ProjectRoleDTO> projectRoles;
}