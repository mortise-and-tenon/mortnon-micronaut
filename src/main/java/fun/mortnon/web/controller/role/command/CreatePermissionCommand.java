package fun.mortnon.web.controller.role.command;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpMethod;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dev2007
 * @date 2023/2/23
 */
@Data
public class CreatePermissionCommand {
    /**
     * 权限名字
     */
    private String name;

    /**
     * 权限标识符
     */
    private String identifier;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 适用 api
     */
    private String api;

    /**
     * api 方法
     */
    private HttpMethod method;
}
