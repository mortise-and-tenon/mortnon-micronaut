package fun.mortnon.web.controller.role.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/22
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class CreateRoleCommand {
    /**
     * 角色名
     */
    @NotNull
    @NotEmpty
    private String name;

    /**
     * 角色标识符
     */
    @NotNull
    @NotEmpty
    private String identifier;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 权限 id 列表
     */
    private List<Long> permissions;

    /**
     * 角色状态
     */
    private boolean status;
}
