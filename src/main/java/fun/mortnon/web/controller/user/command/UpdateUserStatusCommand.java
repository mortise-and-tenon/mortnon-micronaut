package fun.mortnon.web.controller.user.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * 用户状态变更数据
 *
 * @author dev2007
 * @date 2024/3/13
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class UpdateUserStatusCommand {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户状态
     */
    private boolean status;
}
