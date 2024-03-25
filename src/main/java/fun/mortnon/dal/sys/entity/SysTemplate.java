package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.base.entity.BaseEntity;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/3/21
 */
@Serdeable
@MappedEntity
@Data
public class SysTemplate extends BaseEntity {
    private String name;
    private String subject;
    private String content;
    private boolean enabled;
    private boolean system;
}
