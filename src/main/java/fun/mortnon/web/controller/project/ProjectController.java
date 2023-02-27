package fun.mortnon.web.controller.project;

import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysProjectService;
import fun.mortnon.service.sys.SysRoleService;
import fun.mortnon.service.sys.vo.SysProjectDTO;
import fun.mortnon.service.sys.vo.SysRoleDTO;
import fun.mortnon.web.controller.project.command.CreateProjectCommand;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import fun.mortnon.web.controller.role.command.CreateRoleCommand;
import fun.mortnon.web.controller.role.command.UpdateRoleCommand;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 组织/部门
 *
 * @author dev2007
 * @date 2023/2/24
 */
@Controller("/projects")
public class ProjectController {
    @Inject
    private SysProjectService sysProjectService;

    @Get
    public Mono<MortnonResult<PageableData<List<SysProjectDTO>>>> queryRole(@Valid Pageable pageable) {
        return sysProjectService.queryProjects(pageable).map(MortnonResult::successPageData);
    }

    /**
     * 创建组织
     *
     * @param createProjectCommand
     * @return
     */
    @Post
    public Mono<MutableHttpResponse<MortnonResult>> createRole(CreateProjectCommand createProjectCommand) {
        return sysProjectService.createProject(createProjectCommand).map(k -> HttpResponse.created(MortnonResult.success(k)));
    }

    /**
     * 删除组织
     *
     * @param id
     * @return
     */
    @Delete("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> deleteProject(@NotNull Long id) {
        return sysProjectService.deleteProject(id).map(MortnonResult::success).map(HttpResponse::ok);
    }

    /**
     * 修改组织
     * @param updateProjectCommand
     * @return
     */
    @Put
    public Mono<MutableHttpResponse<MortnonResult>> update(@NonNull UpdateProjectCommand updateProjectCommand) {
        return sysProjectService.updateProject(updateProjectCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }

}
