package fun.mortnon.dal.sys.entity;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * 角色和权限关联表
 *
 * @author dev2007
 * @date 2023/2/9
 */
@MappedEntity
@Data
@Serdeable
public class SysRolePermission {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private Long id;

    /**
     * 角色 id
     */
    private Long roleId;

    /**
     * 权限 id
     */
    private Long permissionId;
}
