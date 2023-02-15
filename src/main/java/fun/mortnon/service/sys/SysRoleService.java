package fun.mortnon.service.sys;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.service.sys.vo.RolePermissionDTO;
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
     * @param rolePermissionDTO
     * @return
     */
    Mono<SysRole> saveUser(RolePermissionDTO rolePermissionDTO);

    /**
     * 修改角色
     *
     * @param rolePermissionDTO
     * @return
     */
    Mono<Boolean> modifyUser(RolePermissionDTO rolePermissionDTO);

    /**
     * 查询角色列表
     *
     * @param pageable
     * @return
     */
    Mono<Page<SysRole>> queryRoles(Pageable pageable);
}
