package fun.mortnon.web.controller.project;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysProjectService;
import fun.mortnon.service.sys.SysRoleService;
import fun.mortnon.service.sys.vo.SysProjectDTO;
import fun.mortnon.service.sys.vo.SysProjectTreeDTO;
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
import javax.validation.constraints.Positive;
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

    /**
     * 查询组织
     *
     * @param pageable
     * @return
     */
    @Get
    public Mono<MortnonResult<PageableData<List<SysProjectDTO>>>> queryProject(@Valid Pageable pageable) {
        return sysProjectService.queryProjects(pageable).map(MortnonResult::successPageData);
    }

    /**
     * 查询树型的组织信息
     *
     * @return
     */
    @Get("/tree")
    public Mono<MortnonResult<SysProjectTreeDTO>> queryTreeProject() {
        return sysProjectService.queryTreeProjects().map(MortnonResult::success);
    }

    /**
     * 创建组织
     *
     * @param createProjectCommand
     * @return
     */
    @OperationLog(LogConstants.PROJECT_CREATE)
    @Post
    public Mono<MutableHttpResponse<MortnonResult>> createProject(@Valid CreateProjectCommand createProjectCommand) {
        return sysProjectService.createProject(createProjectCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::created);
    }

    /**
     * 删除组织
     *
     * @param id
     * @return
     */
    @OperationLog(LogConstants.PROJECT_DELETE)
    @Delete("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> deleteProject(@NotNull @Positive Long id) {
        return sysProjectService.deleteProject(id).map(MortnonResult::success).map(HttpResponse::ok);
    }

    /**
     * 修改组织
     *
     * @param updateProjectCommand
     * @return
     */
    @OperationLog(LogConstants.PROJECT_UPDATE)
    @Put
    public Mono<MutableHttpResponse<MortnonResult>> updateProject(@NonNull @Valid UpdateProjectCommand updateProjectCommand) {
        return sysProjectService.updateProject(updateProjectCommand).map(MortnonResult::success).map(HttpResponse::ok);
    }

}
