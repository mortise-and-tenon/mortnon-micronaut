package fun.mortnon.web.controller.menu.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * 菜单搜索字段
 *
 * @author dev2007
 * @date 2024/3/1
 */
@Data
@Serdeable(naming = SnakeCaseStrategy.class)
@Introspected
public class MenuSearch {
    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单状态
     */
    private Boolean status;
}
