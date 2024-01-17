package fun.mortnon.web.controller.project.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author dev2007
 * @date 2023/2/24
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class UpdateProjectCommand {
    /**
     * 组织 id
     */
    @NotNull
    @Positive
    private Long id;

    /**
     * 组织名字
     */
    private String name;

    /**
     * 组织描述
     */
    private String description;

    /**
     * 组织父 id
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long parentId;
}
