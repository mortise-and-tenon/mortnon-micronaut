package fun.mortnon.service.sys.impl;

import fun.mortnon.dal.sys.entity.SysRole;
import fun.mortnon.service.sys.SysRoleService;
import fun.mortnon.service.sys.vo.RolePermissionDTO;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/15
 */
@Singleton
public class SysRoleServiceImpl implements SysRoleService {
    @Override
    public Mono<SysRole> saveUser(RolePermissionDTO rolePermissionDTO) {
        return null;
    }

    @Override
    public Mono<Page<SysRole>> queryRoles(Pageable pageable) {
        return null;
    }
}
