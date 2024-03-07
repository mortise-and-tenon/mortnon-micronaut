package fun.mortnon.web.controller.role.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 角色更新数据
 *
 * @author dev2007
 * @date 2023/2/22
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class UpdateRoleCommand {
    /**
     * 角色 id
     */
    @NotNull
    @Positive
    private Long id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 权限 id 列表
     */
    @NotNull
    private List<Long> permissions;

    /**
     * 角色状态
     */
    private Boolean status;
}
