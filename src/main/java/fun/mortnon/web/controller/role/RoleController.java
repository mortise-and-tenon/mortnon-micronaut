package fun.mortnon.web.controller.role;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.enums.ErrorCodeEnum;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.framework.vo.PageableQuery;
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
import javax.validation.constraints.Positive;
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
    @Get("{?pageable*}")
    public Mono<MortnonResult<PageableData<List<SysRoleDTO>>>> queryRole(PageableQuery pageable) {
        return sysRoleService.queryRoles(pageable.convert()).map(MortnonResult::successPageData);
    }

    /**
     * 创建角色
     *
     * @param createRoleCommand
     * @return
     */
    @OperationLog(LogConstants.ROLE_CREATE)
    @Post
    public Mono<MutableHttpResponse<MortnonResult>> createRole(@Valid CreateRoleCommand createRoleCommand) {
        return sysRoleService.createRole(createRoleCommand).map(k -> HttpResponse.created(MortnonResult.success(k)));
    }

    /**
     * 删除指定角色
     *
     * @param id
     * @return
     */
    @OperationLog(LogConstants.ROLE_DELETE)
    @Delete("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> deleteRole(@NotNull @Positive Long id) {
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
    public Mono<MutableHttpResponse<MortnonResult>> getRole(@NotNull @Positive Long id) {
        return sysRoleService.queryRole(id)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 修改角色
     *
     * @param updateRoleCommand
     * @return
     */
    @OperationLog(LogConstants.ROLE_UPDATE)
    @Put
    public Mono<MutableHttpResponse<MortnonResult>> update(@NonNull @Valid UpdateRoleCommand updateRoleCommand) {
        return sysRoleService.updateRole(updateRoleCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::ok)
                .onErrorReturn(HttpResponse.badRequest(MortnonResult.fail(ErrorCodeEnum.PARAM_ERROR)));
    }
}
