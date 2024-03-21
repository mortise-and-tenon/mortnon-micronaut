package fun.mortnon.service.login.vo;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/3/21
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class LockVO {
    /**
     * 锁定时长
     */
    private long lockTime;
}
