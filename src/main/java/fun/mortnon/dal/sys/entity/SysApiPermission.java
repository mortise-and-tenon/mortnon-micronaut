package fun.mortnon.dal.sys.entity;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.http.HttpMethod;
import lombok.Data;

/**
 * API和权限关联表
 *
 * @author dev2007
 * @date 2023/2/9
 */
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
