package fun.mortnon.web.controller.role;

import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysRoleService;
import fun.mortnon.service.sys.vo.SysRoleDTO;
import fun.mortnon.web.controller.role.command.CreateRoleCommand;
import fun.mortnon.web.controller.role.command.UpdateRoleCommand;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色
 *
 * @author dev2007
 * @date 2023/2/15
 */
@Controller("/roles")
@Slf4j
public class RoleController {
    @Inject
    private SysRoleService sysRoleService;

    /**
     * 查询角色
     *
     * @param pageable
     * @return
     */
    @Get
    public Mono<MortnonResult<PageableData<List<SysRoleDTO>>>> queryRole(@Valid Pageable pageable) {
        return sysRoleService.queryRoles(pageable).map(MortnonResult::successPageData);
    }

    /**
     * 创建角色
     *
     * @param createRoleCommand
     * @return
     */
    @Post
    public Mono<MutableHttpResponse<MortnonResult>> createRole(CreateRoleCommand createRoleCommand) {
        return sysRoleService.createRole(createRoleCommand).map(k -> HttpResponse.created(MortnonResult.success(k)));
    }

    /**
     * 删除指定角色
     *
     * @param id
     * @return
     */
    @Delete("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> deleteRole(@NotNull Long id) {
        return sysRoleService.deleteRole(id)
                .map(result -> result ? HttpResponse.ok(MortnonResult.success(null))
                        : HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }

    /**
     * 查询指定角色
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> getRole(@NotNull Long id) {
        return sysRoleService.queryRole(id)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    @Put
    public Mono<MutableHttpResponse<MortnonResult>> update(@NonNull UpdateRoleCommand updateRoleCommand) {
        return sysRoleService.updateRole(updateRoleCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::ok)
                .onErrorReturn(HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }
}
