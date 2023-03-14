package fun.mortnon.web.controller.role.command;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/22
 */
@Introspected
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
     * 描述
     */
    private String description;

    /**
     * 权限 id 列表
     */
    @NotNull
    private List<Long> permissionList;
}
