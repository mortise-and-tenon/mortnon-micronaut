package fun.mortnon.web.controller.system.command.message;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * @author dev2007
 * @date 2024/4/1
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class TestEmailConfigCommand extends UpdateEmailConfigCommand {
    private String code;
}
