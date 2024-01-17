package fun.mortnon.service.sys.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dev2007
 * @date 2023/2/27
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class ProjectRoleDTO {
    /**
     * 组织 id
     */
    private Long projectId;

    /**
     * 组织名字
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String projectName;

    /**
     * 角色 id
     */
    private Long roleId;

    /**
     * 角色名字
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String roleName;
}
