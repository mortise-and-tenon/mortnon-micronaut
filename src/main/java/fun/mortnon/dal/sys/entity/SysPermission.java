package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.base.entity.BaseEntity;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.http.HttpMethod;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * 用户权限
 *
 * @author dev2007
 * @date 2023/2/9
 */
@Serdeable
@MappedEntity
@Data
public class SysPermission extends BaseEntity {
    /**
     * 权限名字
     */
    private String name;

    /**
     * 权限标识值
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
}
