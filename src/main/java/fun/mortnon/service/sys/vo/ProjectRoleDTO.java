package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/27
 */
@Data
public class ProjectRoleDTO {
    /**
     * 组织 id
     */
    @JsonProperty(value = "project_id")
    private Long projectId;

    /**
     * 组织名字
     */
    @JsonProperty(value = "project_name")
    private String projectName;

    /**
     * 角色 id
     */
    @JsonProperty(value = "role_id")
    private Long roleId;

    /**
     * 角色名字
     */
    @JsonProperty(value = "role_name")
    private String roleName;
}
