package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysRolePermission;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface RolePermissionRepository extends ReactorCrudRepository<SysRolePermission, Long> {
    /**
     * 按角色 id 查找 权限集
     *
     * @param roleId
     * @return
     */
    Flux<SysRolePermission> findByRoleId(Long roleId);

    /**
     * 删除角色对应的权限集
     *
     * @param roleId 角色 id
     * @return
     */
    Mono<Long> deleteByRoleId(Long roleId);

    /**
     * 权限是否被角色使用
     *
     * @param permissionId
     * @return
     */
    Mono<Boolean> existsByPermissionId(Long permissionId);
}
