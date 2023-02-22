package fun.mortnon.web.controller.user.command;

import lombok.Data;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/22
 */
@Data
public class UpdateRoleCommand {
    /**
     * 角色 id
     */
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
    private List<Long> permissionList;
}
