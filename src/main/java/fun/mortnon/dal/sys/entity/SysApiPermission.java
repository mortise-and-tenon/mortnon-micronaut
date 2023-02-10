package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.base.entity.BaseEntity;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.http.HttpMethod;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@Serdeable
@MappedEntity
@Data
public class SysApiPermission {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private Long id;

    private String api;
    private HttpMethod method;
    private String permission;
}
