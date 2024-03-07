package fun.mortnon.web.controller.role.command;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpMethod;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 权限创建数据
 *
 * @author dev2007
 * @date 2023/2/23
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Data
public class CreatePermissionCommand {
    /**
     * 权限名字
     */
    @NotBlank
    private String name;

    /**
     * 权限标识符
     */
    @NotBlank
    private String identifier;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 适用 api
     */
    @NotBlank
    private String api;

    /**
     * api 方法
     */
    @NotNull
    private HttpMethod method;
}
