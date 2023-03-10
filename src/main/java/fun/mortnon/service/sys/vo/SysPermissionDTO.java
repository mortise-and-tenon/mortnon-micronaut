package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysPermission;
import io.micronaut.http.HttpMethod;
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

    /**
     * 权限描述
     */
    private String description;

    /**
     * 适用 api
     */
    private String api;

    /**
     * api 方法
     */
    private HttpMethod method;

    public static SysPermissionDTO convert(SysPermission sysPermission) {
        SysPermissionDTO sysPermissionDTO = new SysPermissionDTO();
        sysPermissionDTO.setId(sysPermission.getId());
        sysPermissionDTO.setIdentifier(sysPermission.getIdentifier());
        sysPermissionDTO.setDescription(sysPermission.getDescription());
        sysPermissionDTO.setApi(sysPermission.getApi());
        sysPermissionDTO.setMethod(sysPermission.getMethod());
        return sysPermissionDTO;
    }
}
