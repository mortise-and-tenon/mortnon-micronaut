package fun.mortnon.web.controller.role;

import fun.mortnon.framework.enums.ErrorCodeEnum;
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
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/20
 */
@Controller("/permissions")
public class PermissionController {

    @Inject
    private SysPermissionService sysPermissionService;

    /**
     * 查询所有权限
     *
     * @param pageable
     * @return
     */
    @Get
    public Mono<MortnonResult<PageableData<List<SysPermissionDTO>>>> queryPermission(@Valid Pageable pageable) {
        return sysPermissionService.queryPermission(pageable).map(MortnonResult::successPageData);
    }

    @Post
    public Mono<MutableHttpResponse> createPermission(@NotNull @Valid CreatePermissionCommand createPermissionCommand) {
        return sysPermissionService.createPermission(createPermissionCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::created);
    }

    @Delete("/{id}")
    public Mono<MutableHttpResponse> deletePermission(Long id) {
        return sysPermissionService.deletePermission(id)
                .map(result -> result ? HttpResponse.ok(MortnonResult.success(null))
                        : HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }
}
