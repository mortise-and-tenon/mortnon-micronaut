package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.base.entity.BaseEntity;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * 组织
 *
 * @author dev2007
 * @date 2023/2/11
 */
@Serdeable
@MappedEntity
@Data
public class SysProject extends BaseEntity {
    /**
     * 组织名字
     */
    private String name;

    /**
     * 组织描述
     */
    private String description;

    /**
     * 父组织 id
     */
    private Long parentId;
}
