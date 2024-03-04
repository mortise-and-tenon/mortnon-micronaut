package fun.mortnon.web.controller.user.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.util.List;

/**
 * @author dev2007
 * @date 2024/3/2
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class AssignmentCommand {
    public AssignmentCommand(){

    }

    public AssignmentCommand(Long userId, Long projectId, Long roleId) {
        this.userId = userId;
        this.projectId = projectId;
        this.roleId = roleId;
    }

    /**
     * 用户 id
     */
    @JsonProperty(value = "user_id")
    private Long userId;

    /**
     * 组织 id
     */
    @JsonProperty(value = "project_id")
    private Long projectId;

    /**
     * 角色 id
     */
    @JsonProperty(value = "role_id")
    private Long roleId;

    @JsonProperty(value = "user_id_list")
    private List<Long> userIdList;
}
