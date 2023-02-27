package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.entity.SysUser;
import fun.mortnon.service.sys.vo.SysProjectDTO;
import fun.mortnon.service.sys.vo.SysUserDTO;
import fun.mortnon.web.controller.project.command.CreateProjectCommand;
import fun.mortnon.web.controller.project.command.UpdateProjectCommand;
import fun.mortnon.web.controller.user.command.CreateUserCommand;
import fun.mortnon.web.controller.user.command.UpdateUserCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import reactor.core.publisher.Mono;

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
     * @param pageable 分页数据
     * @return
     */
    Mono<Page<SysProjectDTO>> queryProjects(Pageable pageable);

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
