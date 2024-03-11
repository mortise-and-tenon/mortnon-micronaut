package fun.mortnon.dal.sys.entity;

import fun.mortnon.dal.base.entity.BaseEntity;
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
public class SysApi extends BaseEntity {
    /**
     * 权限标识值
     */
    private String identifier;

    /**
     * 适用 API
     */
    private String api;

    /**
     * API 方法
     */
    private HttpMethod method;
}
