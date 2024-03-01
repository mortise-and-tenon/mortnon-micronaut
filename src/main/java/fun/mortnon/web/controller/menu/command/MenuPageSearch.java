package fun.mortnon.web.controller.menu.command;

import fun.mortnon.framework.vo.PageableQuery;
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
public class MenuPageSearch extends PageableQuery {
    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单状态
     */
    private Boolean status;
}
