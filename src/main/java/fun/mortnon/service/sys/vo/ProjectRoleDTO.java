package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dev2007
 * @date 2023/2/27
 */
@Data
public class ProjectRoleDTO {
    /**
     * 组织 id
     */
    private Long projectId;

    /**
     * 组织名字
     */
    private String projectName;

    /**
     * 角色 id
     */
    private Long roleId;

    /**
     * 角色名字
     */
    private String roleName;
}
