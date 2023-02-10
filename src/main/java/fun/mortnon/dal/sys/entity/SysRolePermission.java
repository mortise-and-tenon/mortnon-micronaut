package fun.mortnon.dal.sys.entity;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@Serdeable
@MappedEntity
@Data
public class SysRolePermission {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private Long id;

    private Long roleId;

    private Long permissionId;
}
