package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysPermission;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/17
 */
@Data
public class SysPermissionDTO {
    /**
     * 权限 id
     */
    private Long id;

    /**
     * 权限标识符
     */
    private String identifier;

    public static SysPermissionDTO convert(SysPermission sysPermission) {
        SysPermissionDTO sysPermissionDTO = new SysPermissionDTO();
        sysPermissionDTO.setId(sysPermission.getId());
        sysPermissionDTO.setIdentifier(sysPermission.getIdentifier());
        return sysPermissionDTO;
    }
}
