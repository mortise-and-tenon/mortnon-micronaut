package fun.mortnon.service.sys;

import fun.mortnon.service.sys.vo.SysProjectDTO;
import fun.mortnon.service.sys.vo.SysProjectTreeDTO;
import fun.mortnon.web.controller.project.command.CreateProjectCommand;
import fun.mortnon.web.controller.project.command.ProjectPageSearch;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import io.micronaut.data.model.Page;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/24
 */
public interface SysProjectService {

    /**
     * 保存组织
     *
     * @param createProjectCommand
     * @return
     */
    Mono<SysProjectDTO> createProject(CreateProjectCommand createProjectCommand);

    /**
     * 查询组织列表
     *
     * @param pageSearch 搜索字段
     * @return
     */
    Mono<Page<SysProjectDTO>> queryProjects(ProjectPageSearch pageSearch);

    /**
     * 按ID查询组织
     *
     * @param id
     * @return
     */
    Mono<SysProjectDTO> queryProjectById(Long id);

    /**
     * 查询树型的全部组织数据
     *
     * @param pageSearch 搜索字段
     * @return
     */
    Mono<List<SysProjectTreeDTO>> queryTreeProjects(ProjectPageSearch pageSearch);

    /**
     * 删除组织
     *
     * @param id
     * @return
     */
    Mono<Boolean> deleteProject(Long id);

    /**
     * 更新组织
     *
     * @param updateProjectCommand
     * @return
     */
    Mono<SysProjectDTO> updateProject(UpdateProjectCommand updateProjectCommand);
}
