package fun.mortnon.service.sys.vo;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import java.util.List;

/**
 * @author dev2007
 * @date 2024/3/11
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class ProfileDTO {
    private SysUserDTO user;

    private List<String> permission;

    private List<SysMenuTreeDTO> menu;
}
