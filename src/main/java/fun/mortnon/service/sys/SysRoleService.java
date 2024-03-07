package fun.mortnon.service.sys;

import fun.mortnon.service.sys.vo.SysRoleDTO;
import fun.mortnon.web.controller.role.command.CreateRoleCommand;
import fun.mortnon.web.controller.role.command.RolePageSearch;
import fun.mortnon.web.controller.role.command.UpdateRoleCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import reactor.core.publisher.Mono;

/**
 * 角色服务
 *
 * @author dev2007
 * @date 2023/2/15
 */
public interface SysRoleService {

    /**
     * 创建角色
     *
     * @param createRoleCommand
     * @return
     */
    Mono<SysRoleDTO> createRole(CreateRoleCommand createRoleCommand);

    /**
     * 修改角色
     *
     * @param updateRoleCommand
     * @return
     */
    Mono<SysRoleDTO> updateRole(UpdateRoleCommand updateRoleCommand);

    /**
     * 查询角色列表
     *
     * @param pageSearch
     * @return
     */
    Mono<Page<SysRoleDTO>> queryRoles(RolePageSearch pageSearch);


    /**
     * 查询指定角色
     *
     * @param id
     * @return
     */
    Mono<SysRoleDTO> queryRole(Long id);

    /**
     * 删除角色
     *
     * @param id
     * @return
     */
    Mono<Boolean> deleteRole(Long id);
}
