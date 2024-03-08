package fun.mortnon.web.controller.project;

import fun.mortnon.framework.aop.OperationLog;
import fun.mortnon.framework.constants.LogConstants;
import fun.mortnon.framework.vo.MortnonResult;
import fun.mortnon.framework.vo.PageableData;
import fun.mortnon.service.sys.SysProjectService;
import fun.mortnon.service.sys.vo.SysProjectDTO;
import fun.mortnon.service.sys.vo.SysProjectTreeDTO;
import fun.mortnon.web.controller.project.command.CreateProjectCommand;
import fun.mortnon.web.controller.project.command.ProjectPageSearch;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
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
    /**
     * 组织服务
     */
    @Inject
    private SysProjectService sysProjectService;

    /**
     * 查询组织
     *
     * @param pageSearch 查询参数、分页、排序
     * @return
     */
    @Get("{?pageSearch*}")
    public Mono<MortnonResult<PageableData<List<SysProjectDTO>>>> queryProject(ProjectPageSearch pageSearch) {
        return sysProjectService.queryProjects(pageSearch)
                .map(MortnonResult::successPageData);
    }

    /**
     * 查询树型的组织信息
     *
     * @param pageSearch 查询参数、分页、排序
     * @return
     */
    @Get("/tree{?pageSearch*}")
    public Mono<MortnonResult<List<SysProjectTreeDTO>>> queryTreeProject(ProjectPageSearch pageSearch) {
        return sysProjectService.queryTreeProjects(pageSearch)
                .map(MortnonResult::success);
    }

    /**
     * 查询指定组织
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    public Mono<MortnonResult<PageableData<List<SysProjectDTO>>>> queryProjectById(@NotNull @Positive Long id) {
        return sysProjectService.queryProjectById(id)
                .map(MortnonResult::success);
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
     * @param id 组织 id
     * @return
     */
    @OperationLog(LogConstants.PROJECT_DELETE)
    @Delete("/{id}")
    public Mono<MutableHttpResponse<MortnonResult>> deleteProject(@NotNull @Positive Long id) {
        return sysProjectService.deleteProject(id)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

    /**
     * 修改组织
     *
     * @param updateProjectCommand 修改组织数据
     * @return
     */
    @OperationLog(LogConstants.PROJECT_UPDATE)
    @Put
    public Mono<MutableHttpResponse<MortnonResult>> updateProject(@NonNull @Valid UpdateProjectCommand updateProjectCommand) {
        return sysProjectService.updateProject(updateProjectCommand)
                .map(MortnonResult::success)
                .map(HttpResponse::ok);
    }

}
