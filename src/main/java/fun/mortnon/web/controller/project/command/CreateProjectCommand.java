package fun.mortnon.web.controller.project.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/24
 */
@Data
public class CreateProjectCommand {
    /**
     * 组织名字
     */
    private String name;

    /**
     * 组织描述
     */
    private String description;

    /**
     * 父组织 id
     */
    @JsonProperty("parent_id")
    private Long parentId;
}
