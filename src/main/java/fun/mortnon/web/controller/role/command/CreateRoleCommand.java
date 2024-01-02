package fun.mortnon.web.controller.role.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/22
 */
@Introspected
@Serdeable
@Data
public class CreateRoleCommand {
    /**
     * 角色名
     */
    @NotNull
    @NotEmpty
    private String name;

    /**
     * 标识符
     */
    @NotNull
    @NotEmpty
    private String identifier;

    /**
     * 描述
     */
    private String description;

    /**
     * 权限 id 列表
     */
    @NotNull
    private List<Long> permissionList;
}
