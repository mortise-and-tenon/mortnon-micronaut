package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysRole;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/17
 */
@Introspected
@Serdeable
@Data
public class SysRoleDTO {
    /**
     * 角色 id
     */
    private Long id;

    /**
     * 角色名字
     */
    private String name;

    /**
     * 角色标识符
     */
    private String identifier;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色权限集
     */
    private List<SysPermissionDTO> permissions;

    public static SysRoleDTO convert(SysRole sysRole) {
        SysRoleDTO sysRoleDTO = new SysRoleDTO();
        sysRoleDTO.setId(sysRole.getId());
        sysRoleDTO.setName(sysRole.getName());
        sysRoleDTO.setIdentifier(sysRole.getIdentifier());
        sysRoleDTO.setDescription(sysRole.getDescription());
        return sysRoleDTO;
    }
}
