package fun.mortnon.web.controller.role.command;

import fun.mortnon.framework.vo.PageableQuery;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * 角色分页查询条件
 *
 * @author dev2007
 * @date 2024/3/5
 */
@Data
@Serdeable(naming = SnakeCaseStrategy.class)
@Introspected
public class RolePageSearch extends PageableQuery {

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色标识值
     */
    private String identifier;

    /**
     * 角色状态
     */
    private Boolean status;
}
