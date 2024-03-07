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
import fun.mortnon.web.controller.role.command.RolePageSearch;
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
    /**
     * 角色服务
     */
    @Inject
    private SysRoleService sysRoleService;

    /**
     * 查询角色
     *
     * @param pageSearch 查询参数、分页、排序
     * @return
     */
    @Get("{?pageSearch*}")
    public Mono<MortnonResult<PageableData<List<SysRoleDTO>>>> queryRole(RolePageSearch pageSearch) {
        return sysRoleService.queryRoles(pageSearch)
                .map(MortnonResult::successPageData);
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
     * 创建角色
     *
     * @param createRoleCommand 创建角色数据
     * @return
     */
    @OperationLog(LogConstants.ROLE_CREATE)
    @Post
    public Mono<MutableHttpResponse<MortnonResult>> createRole(@Valid CreateRoleCommand createRoleCommand) {
        return sysRoleService.createRole(createRoleCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::created);
    }

    /**
     * 修改角色
     *
     * @param updateRoleCommand 角色更新数据
     * @return
     */
    @OperationLog(LogConstants.ROLE_UPDATE)
    @Put
    public Mono<MutableHttpResponse<MortnonResult>> update(@NonNull @Valid UpdateRoleCommand updateRoleCommand) {
        return sysRoleService.updateRole(updateRoleCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 删除指定角色
     *
     * @param id 角色 id
     * @return
     */
    @OperationLog(LogConstants.ROLE_DELETE)
    @Delete("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> deleteRole(@NotNull @Positive Long id) {
        return sysRoleService.deleteRole(id)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }
}
