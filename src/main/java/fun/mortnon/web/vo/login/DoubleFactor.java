package fun.mortnon.web.vo.login;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 双因子验证码生成数据
 *
 * @author dev2007
 * @date 2024/4/7
 */
@Data
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
public class DoubleFactor {
    @NotEmpty
    private String username;
}
