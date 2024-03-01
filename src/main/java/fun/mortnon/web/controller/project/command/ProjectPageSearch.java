package fun.mortnon.web.controller.project.command;

import fun.mortnon.framework.vo.PageableQuery;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

/**
 * 组织搜索字段
 *
 * @author dev2007
 * @date 2024/3/1
 */
@Data
@Serdeable(naming = SnakeCaseStrategy.class)
@Introspected
public class ProjectPageSearch extends PageableQuery {
    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门状态
     */
    private Boolean status;
}
