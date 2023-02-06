package fun.mortnon.dal.base.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 逻辑删除实体
 *
 * @author dongfangzan
 * @date 28.4.21 3:47 下午
 */
@Data
public class LogicDeleteEntity extends BaseEntity {

    /**
     * 逻辑删除位
     */
    protected int deleted;

    /**
     * 删除时间
     */
    protected Date gmtDelete;
}
