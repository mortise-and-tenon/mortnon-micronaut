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
     * 组织标识值
     */
    private String identifier;

    /**
     * 组织描述
     */
    private String description;

    /**
     * 父组织 id
     */
    private Long parentId;

    /**
     * 先辈组织 id 序列
     */
    private String ancestors;

    /**
     * 组织排序
     */
    private int order;

    /**
     * 组织状态
     */
    private boolean status;
}
