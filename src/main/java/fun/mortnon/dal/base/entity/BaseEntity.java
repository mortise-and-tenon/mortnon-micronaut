package fun.mortnon.dal.base.entity;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

/**
 * entity基类
 *
 * @author dongfangzan
 * @date 14.4.21 8:17 下午
 */
@Data
public class BaseEntity {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @DateCreated
    protected Instant gmtCreate;

    /**
     * 修改时间
     */
    @DateUpdated
    protected Instant gmtModify;
}
