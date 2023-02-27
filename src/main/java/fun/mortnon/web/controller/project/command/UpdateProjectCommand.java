package fun.mortnon.web.controller.project.command;

import lombok.Data;

/**
 * @author dev2007
 * @date 2023/2/24
 */
@Data
public class UpdateProjectCommand {
    /**
     * 组织 id
     */
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
    private Long parentId;
}
