package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.service.sys.vo.SysRoleDTO;
import fun.mortnon.web.controller.user.command.CreateRoleCommand;
import fun.mortnon.web.controller.user.command.UpdateRoleCommand;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import reactor.core.publisher.Mono;

/**
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
    Mono<SysRoleDTO> saveRole(CreateRoleCommand createRoleCommand);

    /**
     * 修改角色
     *
     * @param updateRoleCommand
     * @return
     */
    Mono<SysRoleDTO> modifyRole(UpdateRoleCommand updateRoleCommand);

    /**
     * 查询角色列表
     *
     * @param pageable
     * @return
     */
    Mono<Page<SysRoleDTO>> queryRoles(Pageable pageable);


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
