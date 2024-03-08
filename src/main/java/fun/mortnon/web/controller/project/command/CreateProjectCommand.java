package fun.mortnon.web.controller.project.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 创建组织数据
 *
 * @author dev2007
 * @date 2023/2/24
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class CreateProjectCommand {
    /**
     * 组织名字
     */
    @NotBlank
    private String name;

    /**
     * 组织描述
     */
    private String description;

    /**
     * 父组织 id
     */
    @NotNull
    @Positive
    private Long parentId;

    /**
     * 组织排序
     */
    private int order;

    /**
     * 组织状态
     */
    private Boolean status;
}
