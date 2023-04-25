package fun.mortnon.dal.sys.entity;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * 用户——角色——组织 关联表
 *
 * @author dev2007
 * @date 2023/2/9
 */
@MappedEntity
@Data
@Serdeable
public class SysAssignment {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 角色 id
     */
    private Long roleId;

    /**
     * 组织 id
     */
    private Long projectId;
}
