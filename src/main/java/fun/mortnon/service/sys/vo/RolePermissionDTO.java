package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysPermission;
import lombok.Data;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/15
 */
@Data
public class RolePermissionDTO {
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
     * 权限列表
     */
    private List<SysPermission> permissionList;
}
