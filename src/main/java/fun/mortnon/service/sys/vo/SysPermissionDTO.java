package fun.mortnon.service.sys.vo;

import fun.mortnon.dal.sys.entity.SysPermission;
import fun.mortnon.framework.utils.MortnonBeanUtils;
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
     * 依赖的权限
     */
    private String dependency;

    public static SysPermissionDTO convert(SysPermission sysPermission) {
        SysPermissionDTO sysPermissionDTO = new SysPermissionDTO();
        MortnonBeanUtils.copy(sysPermission, sysPermissionDTO);
        return sysPermissionDTO;
    }
}
