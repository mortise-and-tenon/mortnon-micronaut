package fun.mortnon.web.controller.menu.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * @author dev2007
 * @date 2023/12/6
 */
@Introspected
@Serdeable
@Data
public class UpdateMenuCommand {
    /**
     * 菜单 id
     */
    private Long id;
    /**
     * 菜单名字
     */
    @NotBlank
    private String name;

    /**
     * 菜单排序
     */
    private Integer order;

    /**
     * 菜单 url
     */
    private String url;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单权限
     */
    @NotEmpty
    private String permission;
}
