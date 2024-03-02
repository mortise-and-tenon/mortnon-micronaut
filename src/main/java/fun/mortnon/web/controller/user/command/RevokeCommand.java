package fun.mortnon.web.controller.user.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/3/2
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class RevokeCommand {

    public RevokeCommand(Long userId, Long projectId, Long roleId) {
        this.userId = userId;
        this.projectId = projectId;
        this.roleId = roleId;
    }

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 组织 id
     */
    private Long projectId;

    /**
     * 角色 id
     */
    private Long roleId;
}
