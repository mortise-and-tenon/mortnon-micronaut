package fun.mortnon.web.controller.project.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author dev2007
 * @date 2023/2/24
 */
@Introspected
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
}
