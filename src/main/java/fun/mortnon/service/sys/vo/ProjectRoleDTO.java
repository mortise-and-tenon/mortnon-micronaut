package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dev2007
 * @date 2023/2/27
 */
@Introspected
@Serdeable
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
