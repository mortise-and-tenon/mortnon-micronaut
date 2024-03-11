package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysPermission;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpMethod;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/17
 */
@Introspected
@Serdeable
@Data
public class SysPermissionDTO {
    /**
     * 权限 id
     */
    private Long id;

    /**
     * 权限名称
     */
    private String name;

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
        sysPermissionDTO.setName(sysPermission.getName());
        sysPermissionDTO.setIdentifier(sysPermission.getIdentifier());
        sysPermissionDTO.setDescription(sysPermission.getDescription());
        return sysPermissionDTO;
    }
}
