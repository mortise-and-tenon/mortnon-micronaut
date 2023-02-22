package fun.mortnon.dal.sys.repository;

import fun.mortnon.dal.sys.entity.SysApiPermission;
import fun.mortnon.dal.sys.entity.SysRolePermission;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactorCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author dev2007
 * @date 2023/2/9
 */
@R2dbcRepository(dialect = Dialect.MYSQL)
public interface RolePermissionRepository extends ReactorCrudRepository<SysRolePermission, Long> {
    Flux<SysRolePermission> findByRoleId(Long roleId);

    /**
     * 删除角色对应的权限集
     *
     * @param roleId 角色 id
     * @return
     */
    Mono<Long> deleteByRoleId(Long roleId);
}
