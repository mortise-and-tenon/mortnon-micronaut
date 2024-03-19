package fun.mortnon.web.controller.menu.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/12/6
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class CreateMenuCommand {
    /**
     * 菜单名字
     */
    @NotBlank
    private String name;

    /**
     * 菜单排序
     */
    private int order;

    /**
     * 菜单 url
     */
    @NotBlank
    private String url;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单权限
     */
    private String permission;

    @NotNull
    @Positive
    private Long parentId;

    /**
     * 菜单状态
     */
    private Boolean status;

}
