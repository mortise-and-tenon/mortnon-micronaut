package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.base.entity.BaseEntity;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * 用户角色
 *
 * @author dev2007
 * @date 2023/2/9
 */
@Serdeable
@MappedEntity
@Data
public class SysRole extends BaseEntity {
    /**
     * 角色名字
     */
    private String name;

    /**
     * 角色标识值
     */
    private String identifier;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色状态
     */
    private boolean status;
}
