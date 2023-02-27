package fun.mortnon.web.controller.role.command;

import lombok.Data;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/22
 */
@Data
public class CreateRoleCommand {
    /**
     * 角色名
     */
    private String name;

    /**
     * 标识符
     */
    private String identifier;

    /**
     * 描述
     */
    private String description;

    /**
     * 权限 id 列表
     */
    private List<Long> permissionList;
}
