package fun.mortnon.web.controller.role;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysPermissionService;
import fun.mortnon.service.sys.vo.SysPermissionDTO;
import fun.mortnon.web.controller.role.command.CreatePermissionCommand;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 权限
 * 暂不会对外提供，也不会直接在业务中使用，先控制认证的用户可以直接访问
 *
 * @author dev2007
 * @date 2023/2/20
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/permissions")
public class PermissionController {

    /**
     * 权限服务
     */
    @Inject
    private SysPermissionService sysPermissionService;

    /**
     * 查询权限
     *
     * @param pageable
     * @return
     */
    @Get
    public Mono<MortnonResult<PageableData<List<SysPermissionDTO>>>> queryPermission(@Valid Pageable pageable) {
        return sysPermissionService.queryPermission(pageable)
                .map(MortnonResult::successPageData);
    }

    /**
     * 创建权限
     *
     * @param createPermissionCommand
     * @return
     */
    @Post
    public Mono<MutableHttpResponse> createPermission(@NotNull @Valid CreatePermissionCommand createPermissionCommand) {
        return sysPermissionService.createPermission(createPermissionCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::created);
    }

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    @Delete("/{id}")
    public Mono<MutableHttpResponse> deletePermission(Long id) {
        return sysPermissionService.deletePermission(id)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }
}
